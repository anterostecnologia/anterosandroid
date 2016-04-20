package br.com.anteros.android.synchronism.communication.protocol;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import br.com.anteros.core.utils.Base64;

@SuppressWarnings("rawtypes")
public class MobileResponse {
	public static final String NOT = "NÃO";
	public static final String OK = "OK";
	// VECTOR[0]

	private String status;

	// VECTOR[1]{TABLENAME, FIELDS...}
	// TableName|Fields
	private String tableName;
	private String[] fields;

	// DADOS A PARTIR DO REGISTRO 2 (VECTOR[2])

	private List<String[]> data;

	private String requestId;

	public MobileResponse() {
		this.fields = new String[0];
		this.data = new ArrayList<String[]>();
		this.status = "OK";
		this.tableName = "";
	}

	public MobileResponse(List retornoFinal) {
		this();
		setFormattedParameters(retornoFinal);
	}

	public MobileResponse(String status, String tableName) {
		this();
		this.status = status;
		this.tableName = tableName;

	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public String[] getFields() {
		return fields;
	}

	public void setFields(String[] fields) {
		this.fields = fields;
	}

	public List getData() {
		return data;
	}

	public void setData(List<String[]> data) {
		this.data = data;
	}

	public int getFieldPosition(String fieldName) {
		if (fieldName != null && fieldName.length() > 0) {
			for (int i = 0; i < fields.length; i++) {
				if (fieldName.equals(fields[i])) {
					return i;
				}
			}
		}
		return -1;
	}

	public boolean isBase64(String blobString) {
		return blobString.startsWith("<X64>");
	}

	public byte[] decodeBase64(String blobString) throws IOException {
		return Base64.decode(blobString.substring(5));
	}

	@SuppressWarnings("unchecked")
	public void setFormattedParameters(List<String[]> list) {
		// [Status] -> 0
		// [TableName] -> 1
		// [Fiel1,Field2,FieldN] -> 2
		// [Data1,Data2,DataN] -> 3 a N

		// Verifica se o Vetor está preenchido
		if (!(list == null)) {
			if (list.size() > 0) {
				// verifica se o vector do status não é nulo
				if (!(list.get(0) == null)) {
					// Seta o Status
					String[] dataTable = (String[]) list.get(0);
					this.setStatus(dataTable[0]);
				}
				// verifica se o vetor da tabela não é nulo
				if (list.size() > 1) {
					if (!(list.get(1) == null)) {
						String[] dataTable = (String[]) list.get(1);
						// seta o nome da Tabela
						this.setTableName(dataTable[0]);
						// Verifica se vector com os fields não é nulo
						if (list.size() > 2) {
							if (!(list.get(2) == null)) {
								dataTable = (String[]) list.get(2);
								// Verifica se o Array de Strings com os fields
								// não é nulo
								if (dataTable != null) {
									if (dataTable.length > 0) {
										fields = dataTable;
										for (int i = 3; i < list.size(); i++) {
											data.add(list.get(i));
										}
									}
								}
							}
						}
					}
				}
			}
		}

	}

	public String getRequestId() {
		return requestId;
	}

	public void setRequestId(String requestId) {
		this.requestId = requestId;
	}

}
