/*
 * Copyright 2016 Anteros Tecnologia
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package br.com.anteros.android.ui.controls.grid.test;

import java.util.Locale;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.widget.Toast;

import br.com.anteros.android.ui.controls.R;
import br.com.anteros.android.ui.controls.grid.DataGrid;
import br.com.anteros.android.ui.controls.grid.DataGridColumn;
import br.com.anteros.android.ui.controls.grid.DataGridField;
import br.com.anteros.android.ui.controls.grid.DataGridListener;
import br.com.anteros.android.ui.controls.grid.DataGridModel;
import br.com.anteros.android.core.util.CanvasUtils;
import br.com.anteros.android.core.util.DrawProperties;
import br.com.anteros.android.core.util.ImageUtils;


public class TesteGrid extends Activity {

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Locale.setDefault(new Locale("pt-BR"));

		/*
		 * int totalRegistros = 100;
		 * 
		 * Object[][] dados = new Object[totalRegistros][15]; int lastStatus =
		 * -1; for (int i = 0; i < totalRegistros; i++) { for (int z = 0; z <
		 * 15; z++) { if (z == 1) dados[i][z] = "" + (i + 1); else if (z == 2)
		 * dados[i][z] = "JO�O " + (i + 1); else if (z == 3) { lastStatus++;
		 * dados[i][z] = lastStatus + ""; if (lastStatus > 2) lastStatus = -1; }
		 * else if (z == 12) // SAL�RIO dados[i][z] = 113202.34 + i; else if (z
		 * == 13) // DATA NASC dados[i][z] = new Date();// "04/04/1972"; else if
		 * (z == 14) // DATA CADASTRO dados[i][z] = new Date(); //
		 * "09/05/2011 15:55"; else dados[i][z] = "valor " + (i + 1) + "/" + (z
		 * + 1);
		 * 
		 * } }
		 * 
		 * DataGridModel dgm = new DataGridModel(new DataGridColumn[] { new
		 * DataGridColumn("SELECTED", "", 25, true, DataGridColumn.SELECTED, "",
		 * CanvasUtils.VCENTER, CanvasUtils.HCENTER), new DataGridColumn("ID",
		 * "C�d", 50, true, DataGridColumn.TEXT, "", CanvasUtils.VCENTER,
		 * CanvasUtils.RIGHT), new DataGridColumn("NAME", "Nome", 100, true),
		 * new DataGridColumn("TP_STATUS", " ", 24, true), new
		 * DataGridColumn("ENDERECO", "Endere�o", 120, true), new
		 * DataGridColumn("CIDADE", "Cidade", 100, true), new
		 * DataGridColumn("UF", "Uf", 30, true), new DataGridColumn("BAIRRO",
		 * "Bairro", 100, true), new DataGridColumn("CEP", "Cep", 120, true,
		 * DataGridColumn.TEXT, "", CanvasUtils.VCENTER, CanvasUtils.RIGHT), new
		 * DataGridColumn("FONE1", "Fone 1", 80, true), new
		 * DataGridColumn("FONE2", "Fone 2", 80, true), new
		 * DataGridColumn("FAX", "Fax", 80, true), new DataGridColumn("SALARIO",
		 * "Sal�rio", 120, true, DataGridColumn.NUMBER, "###,##0.00",
		 * CanvasUtils.VCENTER, CanvasUtils.RIGHT), new
		 * DataGridColumn("DATANASC", "Data Nasc", 100, true,
		 * DataGridColumn.DATE), new DataGridColumn("DATACAD", "Cadastro", 120,
		 * true, DataGridColumn.DATETIME) }, dados);
		 */

		DataGrid view;
		try {
			view = new DataGrid(this, gerarDados());
			final Activity a = this;

			view.setListener(new DataGridListener() {

				public void onTitleClick(DataGridColumn column) {
					CharSequence s = "Clicou no t�tulo da coluna "
							+ column.getColumnName();
					int duration = Toast.LENGTH_SHORT;
					Toast toast = Toast.makeText(a, s, duration);
					toast.show();
				}

				public boolean onDrawField(DataGridField field,
						DrawProperties fieldProperties, Canvas canvas,
						Paint paint) {
					if ("TP_STATUS".equals(field.getFieldName())) {
						Bitmap myBitmap = null;
						if ("A".equals((String) field.getValue())) {
							myBitmap = BitmapFactory.decodeResource(
									getResources(), R.drawable.circle_green);
						} else {
							myBitmap = BitmapFactory.decodeResource(
									getResources(), R.drawable.circle_red);
						}
						myBitmap = ImageUtils
								.getResizedBitmap(myBitmap, 20, 20);
						canvas.drawBitmap(myBitmap, field.getRect().left + 2,
								field.getRect().top + 2, paint);
						return false;
					} else if ("LIMITE".equals(field.getFieldName())) {
						if (Double.parseDouble(field.getValue().toString()) < 1000) {
							fieldProperties.color = Color.RED;
							fieldProperties.fontColor = Color.WHITE;
						} else if ((Double.parseDouble(field.getValue()
								.toString()) >= 1000)
								&& (Double.parseDouble(field.getValue()
										.toString()) <= 5000)) {
							fieldProperties.color = Color.YELLOW;
							fieldProperties.fontColor = Color.RED;
						} else if ((Double.parseDouble(field.getValue()
								.toString()) >= 5000)
								&& (Double.parseDouble(field.getValue()
										.toString()) <= 20000)) {
							fieldProperties.color = Color.GREEN;
							fieldProperties.fontColor = Color.BLUE;
						} else if (Double.parseDouble(field.getValue()
								.toString()) > 20000) {
							fieldProperties.color = Color.BLUE;
							fieldProperties.fontColor = Color.YELLOW;
						}
					}

					return true;
				}
			});

			view.setFixedColumns(0);
			setContentView(view);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public DataGridModel gerarDados() throws Exception {
		Object[][] dados = {
				{ "", "A", 283, "HAPPY FARMA UNIDADE SARANDI II", "24/12/2008",
						"AVENIDA RIO DE JANEIRO", "44", "30257522", "361",
						"JARDIM INDEPENDÊNCIA II", "SARANDI", "PR", 614.60 },
				{ "", "A", 286, "FARMÁCIA MAGISTRAL", "24/12/2008",
						" OSIRES STENGHEL GUIMARÃES", "43", "32651441", "1126",
						"CONJUNTO HABITACIONAL KARINA", "MARINGÁ", "PR",
						1431.80 },
				{ "", "I", 287, "FRUTARIA PALMARES", "24/12/2008",
						"RUA DOS PALMARES", "43", "32651441", "300",
						"JARDIM LIBERDADE", "MARINGÁ", "PR", 0.00 },
				{ "", "A", 291, "LOJA 2000", "24/12/2008", "TUIUTI", "43",
						"32651441", "1574", "VILA MORANGUEIRA", "MARINGÁ",
						"PR", 2977.30 },
				{ "", "A", 292, "MERCADO CRISTAL", "24/12/2008",
						" FLANKLIN DELANO ROOSEVELT", "44", "32651441", "4463",
						"CONJUNTO HABITACIONAL REQUIÃO", "MARINGÁ", "PR",
						2923.80 },
				{ "", "A", 293, "MERCADO NUNHES", "24/12/2008",
						" SETE DE SETEMBRO", "44", "32651441", "580",
						"JARDIM DA GLÓRIA", "MARINGÁ", "PR", 203.70 },
				{ "", "I", 296, "MERCEARIA E PADARIA GUANDÚ", "24/12/2008",
						" RIO GUANDU", "44", "32289601", "413",
						"CONJUNTO RESIDENCIAL BRANCA VIEIRA", "MARINGÁ", "PR",
						7863.38 },
				{ "", "A", 297, "FARMÁCIA NOSSA SENHORA APARECIDA II",
						"24/12/2008", "PARANÁ", "43", "32651441", "641",
						"CENTRO", "JACAREZINHO", "PR", 0.00 },
				{ "", "A", 298, "PANIFICADORA ÁGUA VIVA", "24/12/2008",
						"JOSÉ TORAL QUERUBIM", "44", "32651441", "840",
						"CONJUNTO HABITACIONAL GUAIAPO", "MARINGÁ", "PR",
						6321.18 },
				{ "", "I", 299, "PANIFICADORA VO MARIA", "24/12/2008",
						"MADRE MÔNICA MARIA", "44", "32651441", "348",
						"CONJUNTO HABITACIONAL LEA LEAL", "MARINGÁ", "PR",
						8138.50 },
				{ "", "A", 300, "FARMÁCIA SÃO JUDAS TADEU", "24/12/2008",
						"MAJOR BARBOSA", "43", "35324341", "908", "CENTRO",
						"CAMBARÁ", "PR", 862.52 },
				{ "", "I", 302, "FARMACIA SANTA LUZIA", "24/12/2008",
						"MAJOR BARBOSA", "43", "32651441", "922", "CENTRO",
						"CAMBARÁ", "PR", 817.19 },
				{ "", "A", 303, "MERCADO NOVA ESPERANCA", "24/12/2008",
						"ARAPONGAS", "43", "32651441", "479",
						"JARDIM SÃO LUIZ", "JACAREZINHO", "PR", 17175.13 },
				{ "", "A", 304, "PANIFICADORA TROPICAL", "24/12/2008",
						"FERNANDO BOTARELLI", "43", "32651441", "420",
						"AEROPORTO", "JACAREZINHO", "PR", 21688.44 },
				{ "", "A", 305, "REDE PAPELARIA", "24/12/2008",
						"RUA DR. GENARO RESENDE", "43", "32651441", "1203",
						"CENTRO", "CAMBARÁ", "PR", 1664.08 },
				{ "", "A", 306, "SUPERMERCADO SAO GABRIEL", "24/12/2008",
						"RUA JOÃO MANOEL DOS SANTOS", "43", "35327305", "1649",
						"VILA RUBIM", "CAMBARÁ", "PR", 7488.77 },
				{ "", "I", 307, "SACOLAO DO CARLAO", "24/12/2008",
						"RUA SAO PAULO", "43", "32651441", "1633",
						"VILA RUBIM", "CAMBARÁ", "PR", 8668.20 },
				{ "", "A", 308, "TOQUE VIDEO", "24/12/2008",
						"RUA COSTA JUNIOR", "43", "32651441", "787", "CENTRO",
						"SANTO ANTÔNIO DA PLATINA", "PR", 0.00 },
				{ "", "A", 310, "ANA CONFECCOES", "24/12/2008",
						"AVENIDA PEDRO CARRASCO ALDUAN", "43", "32651441",
						"1184", "CONJUNTO PARIGOT DE SOUZA 3", "LONDRINA",
						"PR", 0.00 },
				{ "", "A", 311, "LANCHES POLO NORTE", "24/12/2008",
						"SAUL ELKIND", "43", "32651441", "5209",
						"JARDIM PARATY", "LONDRINA", "PR", 24.60 },
				{ "", "I", 314, "SUPERMERCADO TODO DIA", "24/12/2008",
						" FLANKLIN DELANO ROOSEVELT", "44", "32651441", "4410",
						"CONJUNTO HABITACIONAL REQUIÃO", "MARINGÁ", "PR",
						2828.05 },
				{ "", "A", 313, "BAZAR FLOR DE LIZ", "24/12/2008",
						"THOMAZ PEREIRA MACHADO", "43", "32651441", "356",
						"CJ PARIGOT DE SOUZA", "LONDRINA", "PR", 12397.22 },
				{ "", "A", 2969, "BAR E LANCHONETE SANTA FE", "1/7/2009",
						"MORANGUEIRA", "44", "32634766", "50", "ZONA 7",
						"MARINGÁ", "PR", 684.00 },
				{ "", "A", 2970, "AUTO POSTO AMBIENTAL", "27/7/2009",
						"MORANGUEIRA", "44", "32679121", "3468",
						"JARDIM ALVORADA III", "MARINGÁ", "PR", 0.00 },
				{ "", "A", 2971, "SALIM LOTERIAS", "27/7/2009", "PEDRO TAQUES",
						"44", "32461869", "", "JARDIM ALVORADA", "MARINGÁ",
						"PR", 0.00 },
				{ "", "I", 2972, "BAR PARANA", "27/7/2009", "PEDRO TAQUES",
						"44", "32675921", "1941", "JARDIM ALVORADA", "MARINGÁ",
						"PR", 97.40 },
				{ "", "A", 2973, "PAPELARIA PEDRO TAQUES", "27/7/2009",
						"PEDRO TAQUES", "44", "32676225", "1971",
						"JARDIM ALVORADA", "MARINGÁ", "PR", 0.00 },
				{ "", "A", 2974, "BAR NM", "27/7/2009", "PEDRO TAQUES", "44",
						"32675950", "2263", "JARDIM ALVORADA", "MARINGÁ", "PR",
						320.00 },
				{ "", "I", 2975, "SACOLAO GALVAO", "27/7/2009", "PEDRO TAQUES",
						"44", "32676612", "1931", "JARDIM ALVORADA", "MARINGÁ",
						"PR", 1283.00 },
				{ "", "A", 2976, "PANIFICADORA VERONEZE", "27/7/2009", "CHILE",
						"44", "32282099", "1740", "JARDIM EBENEZER", "MARINGÁ",
						"PR", 0.00 },
				{ "", "A", 2977, "MERCADO CIANORTE", "24/7/2009",
						"LUCILIO DE HELD", "44", "32282316", "936",
						"JARDIM ALVORADA", "MARINGÁ", "PR", 7276.70 },
				{ "", "A", 2978, "MERCADO MARTINES", "27/7/2009",
						"LUCILIO DE HELD", "44", "32282754", "722",
						"JARDIM ALVORADA", "MARINGÁ", "PR", 0.00 },
				{ "", "A", 2979, "PEDRO S BAR", "27/7/2009", "28 DE JUNHO",
						"44", "32537769", "209", "JARDIM TUPINAMBA", "MARINGÁ",
						"PR", 0.00 },
				{ "", "A", 2980, "MERCEARIA SANTA LUZIA", "27/7/2009",
						"LA PAZ", "44", "32681969", "204", "VILA MORANGUEIRA",
						"MARINGÁ", "PR", 23032.39 },
				{ "", "I", 2981, "BAR E MERCEARIA DO PIEU", "27/7/2009",
						"LA PAZ", "44", "32689821", "112", "VILA MORANGUEIRA",
						"MARINGÁ", "PR", 0.00 },
				{ "", "A", 2983, "SUPERMERCADO CARABELLI", "27/7/2009",
						"SAO DOMINGOS", "44", "32682212", "1486",
						"VILA MORANGUEIRA", "MARINGÁ", "PR", 82.00 },
				{ "", "I", 2984, "BAR DO GORDO", "27/7/2009", "SAO DOMINGOS",
						"44", "32537855", "1393", "VILA MORANGUEIRA",
						"MARINGÁ", "PR", 8953.28 },
				{ "", "A", 2985, "PANIFICADORA E CONFEITARIA BOSSONI",
						"27/7/2009", "SAO DOMINGOS", "44", "32684549", "1426",
						"VILA MORANGUEIRA", "MARINGÁ", "PR", 0.00 },
				{ "", "A", 2987, "MERCADO ROYAL", "25/7/2009",
						"DOUTOR ALEXANDRE RASGULAEFF", "44", "30295111", "116",
						"JARDIM ALVORADA", "MARINGÁ", "PR", 10496.80 },
				{ "", "A", 2988, "LANCHONETE E RESTAURANTE CARGA PESADA",
						"27/7/2009", "COLOMBO", "44", "32462133", "4614",
						"ZONA 7", "MARINGÁ", "PR", 0.00 },
				{ "", "I", 2989, "BANCA SALIM", "27/7/2009",
						"DOUTOR ALEXANDRE RASGULAEFF", "44", "32677590", "155",
						"PARQUE LARANJEIRAS", "MARINGÁ", "PR", 0.00 },
				{ "", "A", 2990, "MERCADO SAO MARCOS", "25/7/2009",
						"DOUTOR ALEXANDRE RASGULAEFF", "44", "32281156",
						"1160", "JARDIM ALVORADA", "MARINGÁ", "PR", 6069.70 },
				{ "", "I", 2992, "LANCHONETE DO ADEMIR", "7/6/2009",
						"LUCILIO DE HELD", "44", "32283481", "1528",
						"JARDIM ALVORADA", "MARINGÁ", "PR", 1971.00 },
				{ "", "I", 2993, "BAR SANTOS", "27/7/2009", "PEDRO TAQUES",
						"44", "32295608", "2844", "JARDIM ALVORADA", "MARINGÁ",
						"PR", 0.00 },
				{ "", "A", 2994, "AUTO POSTO F1", "22/6/2009", "PEDRO TAQUES",
						"44", "32676080", "2041", "JARDIM ALVORADA", "MARINGÁ",
						"PR", 250.00 },
				{ "", "A", 2995, "FEIRÃO DE CARNES ALVORADA", "27/7/2009",
						"PEDRO TAQUES", "44", "32675501", "1936",
						"JARDIM ALVORADA", "MARINGÁ", "PR", 0.00 },
				{ "", "A", 2998, "BAR E MERCEARIA JABATINGA", "27/7/2009",
						"DONA SOPHIA RASGULAEFF", "44", "32677059", "134",
						"JARDIM ALVORADA", "MARINGÁ", "PR", 0.00 },
				{ "", "A", 2999, "CASA DO LANCHE", "27/7/2009",
						"LUCILIO DE HELD", "44", "32282460", "1570",
						"JARDIM ALVORADA", "MARINGÁ", "PR", 0.00 },
				{ "", "A", 3000, "PANIFICADORA PÃO CASEIRO", "27/7/2009",
						"MORANGUEIRA", "44", "32466730", "1056",
						"JARDIM ALVORADA", "MARINGÁ", "PR", 0.00 },
				{ "", "I", 3001, "LANCHONETE FRANJONES", "27/7/2009",
						"MORANGUEIRA", "44", "32633017", "1578",
						"JARDIM ALVORADA", "MARINGÁ", "PR", 0.00 },
				{ "", "A", 3003, "FARMACIA VALFARMA", "27/7/2009", "TUIUTI",
						"44", "32688330", "3328", "PARQUE RESIDENCIAL TUIUTI",
						"MARINGÁ", "PR", 212.70 },
				{ "", "I", 3004, "PANIFICADORA BRASA", "27/7/2009",
						"DONA SOPHIA RASGULAEFF", "44", "32685098", "2265",
						"JARDIM OASIS", "MARINGÁ", "PR", 0.00 },
				{ "", "A", 3005, "BAR E MERCEARIA GRAFITE", "27/7/2009",
						"RIO TOCANTINS", "44", "32686415", "1910",
						"CONJUNTO RESIDENCIAL PAULINO C. FILHO", "MARINGÁ",
						"PR", 0.00 },
				{ "", "I", 3007, "PEIXARIA PIRAJÚ", "27/7/2009", "COLOMBO",
						"44", "32634449", "5030", "JARDIM UNIVERSITARIO",
						"MARINGÁ", "PR", 0.00 },
				{ "", "A", 3008, "MERCADO VIEIRA", "27/7/2009",
						"DONA SOPHIA RASGULAEFF", "44", "30252149", "4746",
						"CONJUNTO RESIDENCIAL PARIGOT DE SOUZA", "MARINGÁ",
						"PR", 0.00 },
				{ "", "A", 3009, "FARMACIA NOVA CENTRO", "7/6/2009",
						"AVENIDA CIDADE DE LEIRIA", "44", "32245076", "261",
						"ZONA 01", "MARINGÁ", "PR", 341.70 },
				{ "", "A", 3010, "AÇOUGUE RIBEIRO", "27/7/2009", "TUIUTI",
						"44", "32684533", "3666", "PARQUE RESIDENCIAL TUIUTI",
						"MARINGÁ", "PR", 0.00 },
				{ "", "A", 3012, "AUTO POSTO APOLO", "27/7/2009", "COLOMBO",
						"44", "30313471", "2888", "ZONA 7", "MARINGÁ", "PR",
						519.20 },
				{ "", "I", 3013, "AUTO POSTO TUIUTI", "24/7/2009", "TUIUTI",
						"44", "30281234", "2160", "PARQUE RESIDENCIAL TUIUTI",
						"MARINGÁ", "PR", 1279.35 },
				{ "", "A", 475, "AGRÍCOLA OURO VERDE", "29/12/2008",
						"AVENIDA ANUNCIATO SONNI", "43", "32651441", "1965",
						"CENTRO", "JANDAIA DO SUL", "PR", 0.00 },
				{ "", "A", 476, "FARMÁCIA FARMA MED", "29/12/2008",
						"  AMAZONAS", "44", "32651441", "260", "CENTRO",
						"MANDAGUARI", "PR", 3492.99 },
				{ "", "I", 477, "FARMATOTAL", "29/12/2008", " GETULIO VARGAS",
						"43", "32651441", "730", "CENTRO", "JANDAIA DO SUL",
						"PR", 256.80 },
				{ "", "A", 478, "FARMACIA SANTA CATARINA", "29/12/2008",
						"AVENIDA GETULIO VARGAS", "43", "32651441", "531",
						"CENTRO", "JANDAIA DO SUL", "PR", 168.00 },
				{ "", "A", 479, "FARMACIA SAO PAULO", "29/12/2008", "AMAZONAS",
						"44", "32651441", "1350", "CENTRO", "MANDAGUARI", "PR",
						448.60 },
				{ "", "I", 23303, "MINI MERCADO NOSSO ", "22/7/2010",
						"PION JOSÉ BALAN ", "44", "32687709", "675 ",
						"PARQUE RESÍD. AEROPORTO ", "MARINGÁ", "PR", 50.00 },
				{ "", "A", 23304, "LOTERIAS CARAIBAS", "22/7/2010", "CARAIBAS",
						"43", "99222306", "250", "VILA CASONE", "LONDRINA",
						"PR", 272.60 },
				{ "", "A", 1815, "FELIPE CABELEREIRO", "9/4/2009",
						"RUA ANAMBÉ", "44", "32651441", "58", "VILA NOVA",
						"ARAPONGAS", "PR", 383.50 },
				{ "", "I", 1816, "FARMÁCIA FÓRMULA EXATA", "9/4/2009",
						"AVENIDA 07 DE SETEMBRO", "44", "32651441", "488",
						"CENTRO", "ENGENHEIRO BELTRÃO", "PR", 0.00 },
				{ "", "A", 1818, "PANIFICADORA DOCE PÃO", "9/4/2009",
						"RUA FRUXU  SERRANO", "44", "32651441", "22",
						"JARDIM BOA VISTA", "ARAPONGAS", "PR", 1169.70 },
				{ "", "A", 1821, "PANIFICADORA E CONFEITARIA PÃO DE AÇUCAR",
						"9/4/2009", "14 DE DEZEMBRO", "44", "32651441", "845",
						"CENTRO", "NOVA ESPERANÇA", "PR", 249.70 },
				{ "", "A", 1822, "SAINT DIEGOS BAR", "9/4/2009",
						"AVENIDA BRASIL", "44", "32651441", "", "CENTRO",
						"PARANACITY", "PR", 0.00 },
				{ "", "A", 1823, "MERCEARIA BERTONI", "9/4/2009", "BRASIL",
						"44", "32651441", "988", "CENTRO", "PARANACITY", "PR",
						1009.50 },
				{ "", "I", 1826, "MERCEARIA DOIS GAROTOS", "9/4/2009",
						" GUAIAPÓ", "44", "32651441", "2905",
						"JARDIM  LIBERDADE", "MARINGÁ", "PR", 339.52 },
				{ "", "A", 1827, "BAR ARCON", "9/4/2009", "RUA CAXAMBU", "44",
						"30288498", "", "JARDIM ALVORADA", "MARINGÁ", "PR",
						27.50 },
				{ "", "A", 1829, "PANIFICADORA E CONFEITARIA KIPÃO",
						"9/4/2009", "MARANHÃO", "44", "36292051", "925",
						"VILA SETE", "CIANORTE", "PR", 2152.10 },
				{ "", "A", 1831, "MANIA ", "9/4/2009", "AVENIDA BRASIL", "44",
						"99449733", "1446", "CENTRO", "RONDON", "PR", 11135.03 },
				{ "", "A", 1834, "FARMÁCIA CANÃA", "9/4/2009",
						"AVENIDA MARINGÁ", "44", "32651441", "2383",
						"JARDIM NOVA PAULISTA", "SARANDI", "PR", 415.00 },
				{ "", "I", 1835, "FARMÁCIA SANTA HELENA", "9/4/2009",
						"AVENIDA IVAÍ", "44", "32651441", "", "CENTRO",
						"PAIÇANDU", "PR", 0.00 },
				{ "", "A", 1837, "LANCHONETE DA AREL", "9/4/2009",
						"RUA HENRIQUE DIAS", "44", "32651441", "",
						"NOVA LONDRES", "LONDRINA", "PR", 0.00 },
				{ "", "A", 1840, "MERCADO BERGAMINI", "9/4/2009",
						"ANTÔNIO MARTINS PINHÃO", "44", "32651441", "214",
						"VILA BELA VISTA", "BANDEIRANTES", "PR", 4275.12 },
				{ "", "I", 1842, "MERCADINHO OPERÁRIA", "9/4/2009",
						"PAISSANDU", "44", "32276913", "779", "ZONA 03",
						"MARINGÁ", "PR", 3089.33 },
				{ "", "A", 1878, "MERCADINHO PAULISTA", "16/4/2009",
						"RUA TIO TIO", "44", "32651441", "13",
						"JARDIM PAULISTA", "ARAPONGAS", "PR", 1218.66 },
				{ "", "A", 1879, "EMPÓRIO PADRE CHICO", "16/4/2009",
						"RUA SABIÁ-DO-BREJO", "44", "32651441", "128",
						"CONJUNTO PADRE CHICO", "ARAPONGAS", "PR", 7426.82 },
				{ "", "A", 1880, "MERCADO SANTO ANTÔNIO L", "16/4/2009",
						"RUA AURA", "44", "32527352", "323", "VILA NOVA",
						"ARAPONGAS", "PR", 2274.30 },
				{ "", "A", 1881, "FARMÁCIA CONFIANÇA NOVO", "16/4/2009",
						"AVENIDA MINAS GERAIS", "44", "32651441", "460",
						"CENTRO", "JAGUAPITÃ", "PR", 6001.87 },
				{ "", "A", 1882, "FARMÁCIA AVENIDA", "16/4/2009",
						"AVENIDA MINAS GERAIS", "44", "32651441", "86",
						"CENTRO", "JAGUAPITÃ", "PR", 773.70 },
				{ "", "A", 1883, "BAR CONCHEGO", "16/4/2009", " CAMBACICA",
						"44", "32651441", "5", "JARDIM BANDEIRANTES",
						"ARAPONGAS", "PR", 3460.25 },
				{ "", "I", 1884, "PANIFICADORA PRATES", "16/4/2009",
						"AVENIDA SIRIEMA", "44", "32651441", "725",
						"VILA ARAPONGUINHA", "ARAPONGAS", "PR", 8273.94 },
				{ "", "A", 1885, "BANCA VIDE ART NEWS", "16/4/2009",
						"RUA GATURAMO", "44", "32651441", "1136",
						"JARDIM PRIMAVERA", "ARAPONGAS", "PR", 69870.60 },
				{ "", "A", 1886, "BAR E LANCHONETE PAULISTA", "16/4/2009",
						"RUA GRALHA-AZUL", "44", "32651441", "380",
						"VILA EDIO", "ARAPONGAS", "PR", 1361.50 },
				{ "", "I", 1887, "LANCHONETE DA DORA", "16/4/2009",
						"AVENIDA PARANÁ", "44", "32651441", "491", "CENTRO",
						"JAGUAPITÃ", "PR", 7.50 },
				{ "", "A", 1888, "BIA LANCHES", "16/4/2009", "SÃO FRANCISCO",
						"44", "32651441", "240", "JARDIM CAIRI", "COLORADO",
						"PR", 3721.40 },
				{ "", "A", 1889, "ENCANTO S  ACESSÓRIOS FEMININOS",
						"16/4/2009", "SANTOS DUMONT", "44", "32651441", "3063",
						"CENTRO", "MARINGÁ", "PR", 37.50 },
				{ "", "A", 1890, "ESTAÇÃO DO LANCHE", "16/4/2009",
						"AVENIDA GETÚLIO VARGAS", "44", "32651441", "77",
						"ZONA 01", "MARINGÁ", "PR", 12.00 },
				{ "", "I", 1891, "BAR E MERCEARIA VERONEZZI", "16/4/2009",
						"RUA ORLÂNDIA", "44", "32651441", "768",
						"PARQUE DAS LARANJEIRAS", "MARINGÁ", "PR", 32.00 },
				{ "", "A", 1892, "LANCHONETE SORVETERIA DO ZUZU", "16/4/2009",
						"AVENIDA MANDACARU", "44", "32651441", "2216",
						"PARQUE DAS LARANJEIRAS", "MARINGÁ", "PR", 94.30 },
				{ "", "A", 1894, "PULGA LANCHES", "16/4/2009",
						"AVENIDA VEREADOR SÍLVIO ALVES", "44", "32651441",
						"S/N", "JARDIM PIONEIRO", "PAIÇANDU", "PR", 23882.85 },
				{ "", "I", 1895, "MAIS Q BELLA COSMÉTICOS", "16/4/2009",
						"AVENIDA IVAÍ", "44", "32651441", "1210", "CENTRO",
						"PAIÇANDU", "PR", 1876.67 },
				{ "", "A", 1896, "LINCON CONFECÇÕES", "16/4/2009",
						"AVENIDA VEREADOR SILVIO ALVES", "44", "32651441",
						"1155", "JARDIM PIONEIRO", "PAIÇANDU", "PR", 1485.85 },
				{ "", "A", 1460, "FUTURA PAPELARIA E LIVRARIA", "1/4/2009",
						"AVENIDA BOLIVAR", "44", "32651441", "395", "CENTRO",
						"JAPURÁ", "PR", 168.80 },
				{ "", "A", 1462, "SUPERMERCADO ELDORADO", "1/4/2009",
						" BRASIL", "44", "32651441", "424", "CENTRO",
						"TERRA BOA", "PR", 10.00 },
				{ "", "A", 1463, "MERCEARIA MARQUES", "1/4/2009",
						"LIBERO BADARO", "44", "32651441", "323", "CENTRO",
						"JUSSARA", "PR", 91.08 },
				{ "", "A", 1464, "PANIFICADORA VITORIA", "1/4/2009",
						"AVENIDA DOUTOR GASTAO DE MESQUITA FILHO", "44",
						"32651441", "952", "CENTRO", "JUSSARA", "PR", 0.00 },
				{ "", "I", 1465, "PANIFICADORA E CONFEITARIA CAFE CREMOSO",
						"1/4/2009", "AVENIDA MELVIN JONES", "44", "32651441",
						"274", "CENTRO", "TERRA BOA", "PR", 0.00 },
				{ "", "A", 1467, "PASTELARIA HAPPY HOUR", "1/4/2009",
						"GASTAO VIDIGAL", "44", "32651441", "536", "CENTRO",
						"JUSSARA", "PR", 0.00 },
				{ "", "A", 1468, "SORVETERIA GEBON", "1/4/2009", "BRASIL",
						"44", "32651441", "1007", "CENTRO", "TERRA BOA", "PR",
						75.00 },
				{ "", "A", 1470, "VIACAO REAL", "1/4/2009", "RUA DOS SUTIS",
						"44", "32651441", "S/N", "CENTRO", "JAPURÁ", "PR",
						1776.50 },
				{ "", "I", 1471, "XEROCADORA JUSSARA", "1/4/2009",
						"PRAÇA VALDEMAR ALVES NOGUEIRA", "44", "32651441",
						"138", "CENTRO", "JUSSARA", "PR", 1999.25 },
				{ "", "I", 1473, "SORVETERIA HAPPY DAY", "1/4/2009",
						"RUA LINDALVA SILVA BASSETO", "44", "32651441", "1074",
						"ALTO DA BOA VISTA", "LONDRINA", "PR", 0.00 },
				{ "", "I", 1474, "ZIZI PRESENTES", "1/4/2009",
						"AVENIDA SAUL ELKIND", "44", "32651441", "3915",
						"ALTO DA BOA VISTA", "LONDRINA", "PR", 0.00 },
				{ "", "A", 1475, "SALIM LOTERIAS", "1/4/2009",
						"AVENIDA KAKOGAWA", "44", "32651441", "1876",
						"PARQUE DAS GREVÍLEAS", "MARINGÁ", "PR", 1568.00 },
				{ "", "A", 1476, "DISNEY LANCHES", "1/4/2009",
						"AVENIDA PEDRO TAQUES", "44", "32651441", "2934",
						"JARDIM ALVORADA", "MARINGÁ", "PR", 701.85 },
				{ "", "A", 1478, "PANIFICADORA DUE SORELLI", "1/4/2009",
						"AVENIDA LUCÍLIO DE HELD", "44", "32651441", "1719",
						"JARDIM ALVORADA", "MARINGÁ", "PR", 111.00 },
				{ "", "A", 1479, "PANIFICADORA TRIGOPAN", "1/4/2009",
						"AVENIDA DOUTOR ALEXANDRE RASGULAEFF", "44",
						"32651441", "227", "JARDIM REAL", "MARINGÁ", "PR",
						1234.60 },
				{ "", "A", 1510, "DRIKA PRESENTES", "2/4/2009",
						"RUA MÁRIO PEREIRA DE MELLO", "44", "32651441", "74",
						"CONJUNTO PROFESSORA HILDA MANDARINO", "LONDRINA",
						"PR", 0.00 },
				{ "", "A", 1511, "FARO RACOES", "2/4/2009",
						"RUA ARCINDO SARDO", "44", "32651441", "461",
						"JARDIM DAS AMÉRICAS", "LONDRINA", "PR", 0.00 },
				{ "", "A", 1512, "MERCADO JR", "2/4/2009", "RUA OSMY MUNIZ",
						"44", "32651441", "180",
						"CONJUNTO PROFESSORA HILDA MANDARINO", "LONDRINA",
						"PR", 1357.75 },
				{ "", "A", 1514, "MINI MERCADO PORTAL", "2/4/2009",
						"AVENIDA ARCINDO SARDO", "44", "32651441", "1317",
						"WALDEMAR HAUER", "LONDRINA", "PR", 0.00 },
				{ "", "A", 3939, "SUPERMERCADO BOLDRIN", "28/7/2009",
						"AVENIDA DOUTOR CICERO DE MORAES", "0", "4335511139",
						"46", "CENTRO", "SÃO PEDRO DO IVAÍ", "PR", 0.00 },
				{ "", "A", 20580, "SUPERMERCADO FAMA", "31/8/2009",
						"29 DE NOVEMBRO", "44", "35621196", "426", "CENTRO",
						"ARARUNA", "PR", 50.00 },
				{ "", "A", 3942, "SANDRA RODRIGUES DA SILVA", "28/7/2009",
						"AVENIDA DESEMBARGADOR MUNHOZ DE MELO", "0",
						"4434253958", "1521", "CENTRO", "LOANDA", "PR", 0.00 },
				{ "", "A", 3943, "BANCA SL", "28/7/2009",
						"OURO BRANCO ESQ. PLATINA", "43", "99282931", "",
						"CENTRO", "APUCARANA", "PR", 0.00 },
				{ "", "A", 3944, "PANIFICADORA KIRI", "28/7/2009", "KIRI",
						"44", "32465971", "450", "PARQUE DAS PALMEIRAS",
						"MARINGÁ", "PR", 0.00 },
				{ "", "A", 3945, "REDE SAMUARA", "28/7/2009", "MASSUD AMIN",
						"43", "35245232", "Sn", "CENTRO", "CORNÉLIO PROCÓPIO",
						"PR", 17.00 },
				{ "", "A", 3946, "MERCADO HERANCA", "1/7/2009",
						"DANCADOR ESTRELA", "43", "32751202", "112",
						"ALTO DA BOA VISTA", "ARAPONGAS", "PR", 0.00 },
				{ "", "A", 3947, "CONFEITARIA QUIDOCE", "28/7/2009", "BRASIL",
						"44", "32622805", "4295", "CENTRO", "MARINGÁ", "PR",
						0.00 },
				{ "", "A", 3948, "LANGERIE DO MOMENTO", "28/7/2009",
						"GETULIO VARGAS", "43", "34292198", "47", "CENTRO",
						"CALIFÓRNIA", "PR", 0.00 },
				{ "", "A", 3952, "PANIFICADORA REAL", "28/7/2009",
						"IVAN FERREIRA AMARAL", "43", "34351371", "168",
						"CENTRO", "MANOEL RIBAS", "PR", 0.00 },
				{ "", "I", 3953, "FARMACIA PARANAPOEMA", "28/7/2009",
						"AVENIDA PARANAPANEMA", "44", "3342117", "357",
						"CENTRO", "PARANAPOEMA", "PR", 18543.42 },
				{ "", "I", 3954, "CELULAR . COM", "28/7/2009",
						"CARLOS ANTONIO GUERING", "44", "34321110", "799",
						"CENTRO", "NOVA LONDRINA", "PR", 0.00 },
				{ "", "A", 947, "MERCEARIA RECORD", "19/3/2009",
						" DOUTOR RUI CARNASSIALI", "44", "32651441", "55",
						"CONJUNTO HABITACIONAL KARINA", "MARINGÁ", "PR",
						2645.52 },
				{ "", "A", 948, "PANIFICADORA AGUA VIVA", "19/3/2009",
						"RUA JOSÉ TORAL QUERUBIM", "44", "32651441", "840",
						"CONJUNTO HABITACIONAL REQUIÃO", "MARINGÁ", "PR", 0.00 },
				{ "", "A", 949, "QUERO MAIS LANCHES I", "19/3/2009",
						" GUAIAPÓ", "44", "32651441", "3507", "JARDIM OÁSIS",
						"MARINGÁ", "PR", 0.00 },
				{ "", "A", 950, "QUERO MAIS LANCHE II", "19/3/2009",
						"AVENIDA BRASIL", "44", "32651441", "2017",
						"VILA OPERÁRIA", "MARINGÁ", "PR", 0.00 },
				{ "", "A", 951, "VIA IMAGEM FOTO E VIDEO", "19/3/2009",
						" MITSUZO TAGUCHI", "44", "32651441", "73",
						"VILA NOVA", "MARINGÁ", "PR", 589.95 },
				{ "", "A", 952, "BAR DO BAIANO", "19/3/2009", "RUA SURUCUA",
						"44", "32651441", "115", "CONJUNTO CENTAURO",
						"ARAPONGAS", "PR", 200.30 },
				{ "", "A", 953, "BAR DO CARLINHOS", "19/3/2009",
						"RUA SERTANEJA", "44", "32651441", "394",
						"CASA FAMÍLIA ARAPONGAS II", "ARAPONGAS", "PR", 0.00 },
				{ "", "A", 954, "BAR DO CONSENTINO", "19/3/2009",
						"RUA PICA-PAU", "44", "32651441", "1690", "CENTRO",
						"ARAPONGAS", "PR", 1689.10 },
				{ "", "I", 955, "BAR DO FURLAN", "19/3/2009", "RUA TUCANOS",
						"44", "32651441", "1632", "CENTRO", "ARAPONGAS", "PR",
						793.30 },
				{ "", "A", 956, "BAR DO TICO", "19/3/2009", "RUA QUETZAL",
						"44", "32651441", "1387", "VILA ARAPONGUINHA",
						"ARAPONGAS", "PR", 1510.74 },
				{ "", "A", 957, "BAR MASSON", "19/3/2009", "AVENIDA ARAPONGAS",
						"44", "32651441", "1942", "CENTRO", "ARAPONGAS", "PR",
						21.00 },
				{ "", "A", 958, "BAR NOSSA SENHORA APARECIDA", "19/3/2009",
						"RUA FURRIEL", "44", "32651441", "160", "CENTRO",
						"NOVA FÁTIMA", "PR", 0.00 },
				{ "", "A", 959, "BAR PAULISTA", "19/3/2009",
						"AVENIDA ARAPONGAS", "44", "32651441", "25", "CENTRO",
						"ARAPONGAS", "PR", 471.60 },
				{ "", "A", 960, "CASA DE CARNES FIORI II", "19/3/2009",
						"RUA PINTASSILGO", "44", "32651441", "98", "CENTRO",
						"ARAPONGAS", "PR", 0.00 },
				{ "", "A", 961, "CASA DE CARNES PICA PAU", "19/3/2009",
						"RUA PICA-PAU", "44", "32651441", "818", "CENTRO",
						"ARAPONGAS", "PR", 815.40 },
				{ "", "A", 962, "CÉLIO BAR", "19/3/2009", "RUA JURITI", "44",
						"32651441", "1386", "VILA INDUSTRIAL", "ARAPONGAS",
						"PR", 320.25 },
				{ "", "A", 963, "CONFECCOES ELVIRA", "19/3/2009", "RUA QUETE",
						"44", "32651441", "197", "CASA FAMÍLIA ARAPONGAS II",
						"ARAPONGAS", "PR", 293.50 },
				{ "", "A", 964, "DANI PRESENTES", "19/3/2009",
						"RUA TAPERÁ-AÇU", "43", "32755917", "17",
						"VILA SAMPAIO", "ARAPONGAS", "PR", 1490.20 },
				{ "", "A", 965, "ELIZA CONFECCOES", "19/3/2009", "RUA FURRIEL",
						"44", "32651441", "234", "JARDIM COROADOS",
						"ARAPONGAS", "PR", 4674.28 },
				{ "", "A", 966, "FARMACIA EXTRA FARMA", "19/3/2009",
						"AVENIDA ARARAS", "44", "32651441", "40", "CENTRO",
						"ARAPONGAS", "PR", 1019.90 },
				{ "", "A", 967, "FARMACIA NIKKEY IV", "19/3/2009",
						"AVENIDA ARAPONGAS", "44", "32651441", "1575",
						"CENTRO", "ARAPONGAS", "PR", 412.50 },
				{ "", "I", 968, "FARMACIA SAO FRANCISCO", "19/3/2009",
						"AVENIDA ARAPONGAS", "44", "32651441", "835", "CENTRO",
						"ARAPONGAS", "PR", 186.80 },
				{ "", "A", 970, "LAVA RAPIDO FALCAO", "19/3/2009",
						"RUA FALCÃO", "44", "32651441", "776", "CENTRO",
						"ARAPONGAS", "PR", 60.00 },
				{ "", "A", 1001, "BAR DO CARLINHOS", "20/3/2009",
						"RUA IDA POSTALLI VICTORELLI", "44", "33429318", "96",
						"CAFEZAL 1", "LONDRINA", "PR", 4889.00 },
				{ "", "I", 1004, "BAR DO REBE", "20/3/2009",
						"RUA JOAQUIM PEREIRA", "44", "91360435", "",
						"JARDIM DOS ALPES III", "LONDRINA", "PR", 0.00 },
				{ "", "I", 1006, "BAT BANCA", "20/3/2009",
						"RUA RAPOSO TAVARES", "43", "33370587", "", "LARSEN",
						"LONDRINA", "PR", 623.72 },
				{ "", "A", 1008, "CANTINA CATIVA", "20/3/2009", "RUA BÉLGICA",
						"43", "33411905", "", "IGAPÓ", "LONDRINA", "PR", 0.00 },
				{ "", "A", 1009, "CASA DE CARNE PANTANAL", "20/3/2009",
						"RUA MARIA VIDAL DA SILVA", "43", "33488512", "",
						"JARDIM ACAPULCO", "LONDRINA", "PR", 975.20 },
				{ "", "A", 1010, "CONFETERIA ART CAFE", "20/3/2009",
						"AVENIDA RIO DE JANEIRO", "43", "33446684", "123",
						"CENTRO", "LONDRINA", "PR", 0.00 },
				{ "", "A", 1011, "CYBER LERROVILE", "20/3/2009",
						"ELOI NOGEIRA DA SILVA", "43", "33982265", "",
						"LERROVILE", "LONDRINA", "PR", 0.00 },
				{ "", "A", 1012, "CYBER TRONIC", "20/3/2009",
						"AVENIDA CHEPLI TANUS DAHER", "43", "33419197", "",
						"JARDIM ACAPULCO", "LONDRINA", "PR", 147.65 },
				{ "", "I", 1014, "DELICIA GELADA", "20/3/2009",
						"RUA GILBERTO FIERLI", "43", "30266186", "",
						"ESPERANÇA", "LONDRINA", "PR", 37.60 },
				{ "", "A", 1015, "EMPORIO ONLINE", "20/3/2009",
						"RUA SENADOR SOUZA NAVES", "43", "30288686", "",
						"CENTRO", "LONDRINA", "PR", 691.04 },
				{ "", "I", 1016, "FARMACIA ESPLANADA", "20/3/2009",
						"RUA SENADOR SOUZA NAVES", "43", "33225606", "",
						"CENTRO", "LONDRINA", "PR", 7416.41 },
				{ "", "A", 1017, "FARMATIVA", "20/3/2009",
						"AVENIDA PRESIDENTE EURICO GASPAR DUTRA", "43",
						"33426676", "1070", "CAFEZAL 1", "LONDRINA", "PR",
						496.95 },
				{ "", "A", 1019, "MARANATHA PRESENTE", "20/3/2009",
						"AVENIDA BANDEIRANTES", "43", "33245540", "",
						"VILA IPIRANGA", "LONDRINA", "PR", 296.04 },
				{ "", "A", 1020, "MERCADO BOM DIA", "20/3/2009",
						"RUA ERNESTINA DUQUE ESTRADA", "43", "33428054", "118",
						"TAROBÁ", "LONDRINA", "PR", 876.65 },
				{ "", "A", 1021, "MERCADO CAFEZAL", "20/3/2009",
						"AVENIDA CHEPLI TANUS DAHER", "43", "33424471", "",
						"JARDIM ACAPULCO", "LONDRINA", "PR", 115.00 },
				{ "", "A", 1022, "MERCADO VALFRAN", "20/3/2009",
						"RUA ANTÔNIO VIEIRA DA SILVA", "43", "33423668", "250",
						"JARDIM TAROBÁ I", "LONDRINA", "PR", 7418.35 },
				{ "", "A", 1024, "MERCEARIA RODRIGUES", "20/3/2009",
						"RUA BADRI DAGHER", "43", "33424897", "123",
						"JARDIM ACAPULCO", "LONDRINA", "PR", 2001.50 },
				{ "", "I", 1025, "JR DOS SANTOS MERCEARIA", "20/3/2009",
						"RUA KAZUKO HIRAMATSU", "43", "33424295", "",
						"CAFEZAL", "LONDRINA", "PR", 0.00 },
				{ "", "A", 1026, "NAGAI CELULAR E INFORMATICA", "20/3/2009",
						"RUA ARLINDO P. DE ARAUJO", "43", "33980239", "552",
						"CENTRO", "TAMARANA", "PR", 1554.50 },
				{ "", "I", 1079, "MERCADINHO DA INEZ", "24/3/2009",
						"CORONEL FRANCISCO MOREIRA DA COSTA", "43", "35311919",
						"1110", "CENTRO", "SANTA MARIANA", "PR", 10892.41 },
				{ "", "A", 1080, "MERCATUDO", "24/3/2009", "AVENIDA BRASIL",
						"43", "35411888", "316", "CENTRO", "URAÍ", "PR",
						1191.00 },
				{ "", "A", 1085, "PADARIA E CONFEITARIA MARCHIORI",
						"24/3/2009", "RUA SANTA CATARINA", "43", "32324974",
						"900", "CENTRO", "SERTANÓPOLIS", "PR", 9075.70 },
				{ "", "A", 1092, "THECNOLOJA", "24/3/2009",
						"RUA SEBASTIAO VINCE", "43", "35411770", "420",
						"CENTRO", "URAÍ", "PR", 167.72 },
				{ "", "A", 2567, "CASA DOS DOCE", "4/6/2009",
						"BENEDITO SALLES", "44", "32651441", "647", "CENTRO",
						"CARLÓPOLIS", "PR", 63203.16 },
				{ "", "A", 2568, "MERCADO PONTO FINAL", "4/6/2009",
						"BENEDITO SALLES", "44", "32651441", "0", "CENTRO",
						"CARLÓPOLIS", "PR", 49133.32 },
				{ "", "I", 2569, "MERCEARIA ITO", "4/6/2009",
						"BENEDITO SALLES", "44", "32651441", "911", "CENTRO",
						"CARLÓPOLIS", "PR", 25432.28 },
				{ "", "A", 2573, "POSTO CENTRAL", "4/6/2009",
						"BENEDITO SALLES", "44", "32651441", "", "CENTRO",
						"CARLÓPOLIS", "PR", 0.00 },
				{ "", "A", 2575, "POSTO PARAISO", "4/6/2009",
						"BENEDITO SALLES", "44", "32651441", "", "CENTRO",
						"CARLÓPOLIS", "PR", 0.00 },
				{ "", "A", 2581, "POSTO SENTINELA", "4/6/2009",
						"BENEDITO SALLES", "44", "32651441", "", "CENTRO",
						"CARLÓPOLIS", "PR", 0.00 },
				{ "", "I", 2583, "SACOLÃO SIQUEIRENSE", "4/6/2009",
						"RIO GRANDE DO SUL", "44", "32651441", "", "CENTRO",
						"SIQUEIRA CAMPOS", "PR", 0.00 },
				{ "", "A", 2584, "BAR DO LUCIANO", "4/6/2009", "LARANJINHA",
						"44", "32651441", "37", "CENTRO", "CALIFÓRNIA", "PR",
						0.00 },
				{ "", "A", 2770, "BRASIL LAN HOUSE", "8/6/2009",
						"AVENIDA BRASIL", "44", "30352080", "449", "CENTRO",
						"CAMBÉ", "PR", 15.00 },
				{ "", "A", 2771, "FARMÁCIA VIENA II", "8/6/2009",
						"RUA BRASÍLIA", "44", "32651441", "223",
						"PARQUE RESIDENCIAL CAMBÉ", "CAMBÉ", "PR", 958.17 },
				{ "", "A", 2772, "MERCADO BELA VISTA", "8/6/2009", "SALMOS",
						"44", "32651441", "198",
						"CONJUNTO HABITACIONAL DOUTOR JOSÉ DOS SANTOS ROCHA",
						"CAMBÉ", "PR", 7189.73 },
				{ "", "A", 2773, "MERCEARIA DO ARNALDO", "8/6/2009",
						"CESAR  MORESCHI", "44", "32651441", "234",
						"PARQUE RESIDENCIAL ANA ROSA", "CAMBÉ", "PR", 3158.75 },
				{ "", "A", 2774, "PADARIA FATIOLA", "8/6/2009", "RUA SALMOS",
						"44", "32651441", "174",
						"CONJUNTO HABITACIONAL DOUTOR JOSÉ DOS SANTOS ROCHA",
						"CAMBÉ", "PR", 5189.38 },
				{ "", "I", 2775, "J R TEM DE TUDO", "8/6/2009", "AMÉRICA",
						"44", "32651441", "", "CIANORTINHO", "CIANORTE", "PR",
						0.00 },
				{ "", "A", 2776, "MORENO IMPORTADOS", "8/6/2009", "AMÉRICA",
						"44", "32651441", "1265", "CIANORTINHO", "CIANORTE",
						"PR", 85.00 },
				{ "", "A", 2777, "PANIFICADORA VITÓRIA", "8/6/2009", "CORUJA",
						"44", "32651441", "1231", "JARDIM ASA BRANCA II",
						"CIANORTE", "PR", 521.50 },
				{ "", "A", 2778, "AUTO POSTO INDY", "8/6/2009",
						"INDEPENDÊNCIA", "44", "32651441", "Ttt", "CENTRO",
						"BELA VISTA DO PARAÍSO", "PR", 1303.10 },
				{ "", "I", 3588, "SOB NOVA DIREÇÃO IAP", "28/7/2009",
						"IVATUBA KM 6", "44", "32368000", "0", "RODOVIA",
						"FLORESTA", "PR", 0.00 },
				{ "", "A", 20264, "LIMPE BEM", "26/8/2009",
						"MANOEL MENDES DE CAMARGO", "44", "32651441", "2381",
						"CENTRO", "CAMPO MOURÃO", "PR", 15.00 },
				{ "", "A", 3592, "MERCADO M.S", "28/7/2009", "GETULIO VARGAS",
						"44", "32361309", "116", "CENTRO", "FLORESTA", "PR",
						0.00 },
				{ "", "A", 3593, "CASA SÃO JOSÉ", "28/7/2009",
						"GETULIO VARGAS", "44", "32361203", "216", "CENTRO",
						"FLORESTA", "PR", 0.00 },
				{ "", "A", 3594, "MERCADO IVAI", "28/7/2009", "GETULIO VARGAS",
						"44", "32361204", "2002", "CENTRO", "FLORESTA", "PR",
						0.00 },
				{ "", "A", 3595, "BAR E MERCEARIA NEGRETE", "28/7/2009",
						"TIRADENTES", "44", "32361479", "55", "JARDIM CAXIAS",
						"FLORESTA", "PR", 277.20 },
				{ "", "I", 3596, "RESTAURANTE CASEIRO", "20/7/2009",
						"PR-317 KM 025", "44", "32361485", "00", "RODOVIA",
						"FLORESTA", "PR", 4666.21 } };

		DataGridModel dgm = new DataGridModel(new DataGridColumn[] {
				new DataGridColumn("SELECTED", "", 25, true,
						DataGridColumn.SELECTED, "", CanvasUtils.VCENTER,
						CanvasUtils.HCENTER),
				new DataGridColumn("TP_STATUS", " ", 24, true),
				new DataGridColumn("ID", "Cód", 50, true,
						DataGridColumn.NUMBER, "", CanvasUtils.VCENTER,
						CanvasUtils.RIGHT),
				new DataGridColumn("NAME", "Nome", 180, true, true, false),
				new DataGridColumn("DTFUNDACAO", "Data Fund", 100, true,
						DataGridColumn.DATE),
				new DataGridColumn("ENDERECO", "Endereço", 180, true),
				new DataGridColumn("DDD", "DDD", 40, true),
				new DataGridColumn("FONE", "Fone", 100, true),
				new DataGridColumn("NUMERO", "Número", 80, true),
				new DataGridColumn("BAIRRO", "Bairro", 80, true),
				new DataGridColumn("CIDADE", "Cidade", 150, true),
				new DataGridColumn("UF", "UF", 40, true),
				new DataGridColumn("LIMITE", "Lim.Crédito", 150, true,
						DataGridColumn.NUMBER, "#,###,##0.00",
						CanvasUtils.VCENTER, CanvasUtils.RIGHT), }, dados);
		return dgm;

	}

}