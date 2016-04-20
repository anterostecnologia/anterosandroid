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

package br.com.anteros.android.installer;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import br.com.anteros.android.ui.controls.ErrorAlert;
import br.com.anteros.android.ui.controls.InfoAlert;
import br.com.anteros.android.ui.controls.QuestionAlert;
import br.com.anteros.android.ui.controls.QuestionAlert.QuestionListener;

public abstract class ApplicationInstaller extends Activity {
	public int versionCode;
	public String versionName = "";
	public String apkName;
	public String appName;
	public String buildVersionPath = "";
	public String urlpath;
	public String packageName;
	public String installAppPackageName;
	public static final int APPLICATION_FOUND = 1;
	public static final int APPLICATION_NOT_FOUND = 2;
	public static final String OK = "OK";
	public static final String NONE = "NONE";
	public String lastError = NONE;
	public static int REQUEST_INSTALL = 9001;
	public static int REQUEST_UNINSTALL = 9002;
	public static int REQUEST_REINSTALL = 9003;

	protected TextView tvApkStatus;
	protected Button btnCheckUpdates;
	protected Button btnClose;
	protected TextView tvStatus;
	protected TextView tvAndroidInfo;
	protected ProgressBar pbStatus;
	protected ImageView imageView;
	protected View currentView;

	public abstract String getApplicationName();

	public abstract String getAplicationPackageName();

	public abstract String getPackageName();

	public abstract String getUrlVersionFile();

	public abstract String getUrlApplicationPackage();

	public abstract String getMainActivityName();

	public abstract Bitmap getBitmapAppInstaller();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_appinstaller);

		currentView = this.getWindow().getDecorView().findViewById(R.id.mainLayout);

		imageView = (ImageView) findViewById(R.id.imgPackage);

		Bitmap appBitmap = getBitmapAppInstaller();
		if (appBitmap != null) {
			imageView.setImageBitmap(appBitmap);
		}

		appName = getApplicationName();
		apkName = getAplicationPackageName();

		buildVersionPath = getUrlVersionFile();
		packageName = "package:" + getPackageName();
		urlpath = getUrlApplicationPackage();

		tvStatus = (TextView) findViewById(R.id.tvStatus);
		tvAndroidInfo = (TextView) findViewById(R.id.tvAndroidInfo);

		showStatus();

		btnClose = (Button) findViewById(R.id.btnClose);
		btnClose.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});

		btnCheckUpdates = (Button) findViewById(R.id.btnCheckUpdates);
		btnCheckUpdates.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				installOrUpdateApplication();
			}
		});

		imageView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(Intent.ACTION_MAIN);
				intent.setComponent(new ComponentName(getPackageName(), getMainActivityName()));
				startActivity(intent);
			}
		});

	}

	protected void showStatus() {
		if (!isApplicationInstalled())
			tvStatus.setText("A aplicação " + appName + " não está instalada.");
		else
			tvStatus.setText(appName + "\n" + getInstallPackageVersionInfo(appName.toString()));

		tvAndroidInfo.setText("Versão do Android " + android.os.Build.VERSION.RELEASE);
	}

	protected void installOrUpdateApplication() {
		new GetVersionFromServer(this, new OnFinishListener() {

			@Override
			public void onSucess() {
				if (checkInstalledApp(appName.toString()) == true) {
				} else {
					if (NONE.equals(lastError)) {
						new QuestionAlert(ApplicationInstaller.this, "Atenção",
								"Você não possuí a Aplicação " + appName.toString()
										+ " instalada para atualização. Deseja instalá-la?", new QuestionListener() {
									@Override
									public void onPositiveClick() {
										lastError = NONE;
										new DownloadAndInstall().execute(appName, DownloadAndInstall.INSTALL);
									}

									@Override
									public void onNegativeClick() {

									}
								}).show();
					} else
						new ErrorAlert(ApplicationInstaller.this, "Erro", lastError).show();
				}
			}

			@Override
			public void onError() {
			}
		}).execute(buildVersionPath);

	}

	class CustomPackageInformation {
		private String appname = "";
		private String pname = "";
		private String versionName = "";
		private int versionCode = 0;
		private Drawable icon;
	}

	/**
	 * Verifica se uma aplicação está instalada
	 * 
	 * @param appName
	 *            Nome da aplicação
	 * @return true está instalada
	 */
	private Boolean checkInstalledApp(String appName) {
		return getPackages(appName);
	}

	/**
	 * Obtém informações sobre uma aplicação instalada no dispositivo
	 * 
	 * @param appName
	 * @return
	 */
	public String getInstallPackageVersionInfo(String appName) {
		String installVersion = "";
		/*
		 * Não considera pacotes do sistema operacional
		 */
		ArrayList<CustomPackageInformation> apps = getInstalledApps(false);
		final int max = apps.size();
		for (int i = 0; i < max; i++) {
			if (apps.get(i).appname.toString().equals(appName.toString())) {
				installVersion = "Versão instalada: " + apps.get(i).versionName.toString();
				break;
			}
		}
		return installVersion.toString();
	}

	/**
	 * Obtém os pacotes da aplicação
	 * 
	 * @param appName
	 *            Nome da aplicação
	 * @return
	 */
	private Boolean getPackages(final String appName) {
		lastError = NONE;
		Boolean isInstalled = false;
		/*
		 * Não considera pacotes do sistema
		 */
		ArrayList<CustomPackageInformation> apps = getInstalledApps(false);
		final int max = apps.size();
		for (int i = 0; i < max; i++) {
			if (apps.get(i).appname.toString().equals(appName.toString())) {
				if (versionCode <= apps.get(i).versionCode) {
					isInstalled = true;
					DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							switch (which) {
							case DialogInterface.BUTTON_POSITIVE:
								new DownloadAndInstall().execute(packageName, DownloadAndInstall.REINSTALL);
								break;
							case DialogInterface.BUTTON_NEGATIVE:
								break;
							}
						}
					};

					AlertDialog.Builder builder = new AlertDialog.Builder(this);
					AlertDialog alert = builder.setMessage("A aplicação " + appName + " está atualizada. ")
							.setPositiveButton("Reinstalar ?", dialogClickListener)
							.setNegativeButton("Cancelar", dialogClickListener).create();
					alert.setCancelable(false);
					alert.setCanceledOnTouchOutside(false);
					alert.show();
				}
				if (versionCode > apps.get(i).versionCode) {
					isInstalled = true;
					DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							switch (which) {
							case DialogInterface.BUTTON_POSITIVE:
								new DownloadAndInstall().execute(packageName, DownloadAndInstall.REINSTALL);
								break;

							case DialogInterface.BUTTON_NEGATIVE:
								break;
							}
						}
					};

					AlertDialog.Builder builder = new AlertDialog.Builder(this);
					AlertDialog alert = builder
							.setMessage("Há uma nova versão " + versionName + " da aplicação disponível.. Instalar? ")
							.setPositiveButton("Sim", dialogClickListener)
							.setNegativeButton("Não", dialogClickListener).create();
					alert.setCancelable(false);
					alert.setCanceledOnTouchOutside(false);
					alert.show();
				}
			}
		}

		return isInstalled;
	}

	/**
	 * Retorna os pacotes instalados
	 * 
	 * @param getSysPackages
	 *            considerar pacotes do sistema operacional
	 * @return
	 */
	private ArrayList<CustomPackageInformation> getInstalledApps(boolean getSysPackages) {
		ArrayList<CustomPackageInformation> res = new ArrayList<CustomPackageInformation>();
		List<PackageInfo> packs = getPackageManager().getInstalledPackages(0);

		for (int i = 0; i < packs.size(); i++) {
			PackageInfo p = packs.get(i);
			if ((!getSysPackages) && (p.versionName == null)) {
				continue;
			}
			CustomPackageInformation newInfo = new CustomPackageInformation();
			newInfo.appname = p.applicationInfo.loadLabel(getPackageManager()).toString();
			newInfo.pname = p.packageName;
			newInfo.versionName = p.versionName;
			newInfo.versionCode = p.versionCode;
			newInfo.icon = p.applicationInfo.loadIcon(getPackageManager());
			res.add(newInfo);
		}
		return res;
	}

	public void uninstallApplication() {
		uninstallApplication(apkName);
	}

	/**
	 * Desinstala uma aplicação
	 * 
	 * @param packageName
	 *            Nome do pacote para desinstalar
	 */
	public void uninstallApplication(String packageName) {
		Uri packageURI = Uri.parse(packageName.toString());
		Intent uninstallIntent = new Intent(Intent.ACTION_DELETE, packageURI);
		startActivityForResult(uninstallIntent, REQUEST_UNINSTALL);
	}

	/**
	 * Reinstala uma aplicação
	 */
	public void reinstallApplication(String packageName) {
		Uri packageURI = Uri.parse(packageName.toString());
		Intent uninstallIntent = new Intent(Intent.ACTION_DELETE, packageURI);
		startActivityForResult(uninstallIntent, REQUEST_REINSTALL);
	}

	/**
	 * Instala uma aplicação
	 * 
	 * @throws Exception
	 */
	public void installApplication() throws Exception {
		/*
		 * Verifica se o aparelho permite instalar aplicações desconhecidas
		 */
		int result = Settings.Secure.getInt(getContentResolver(), Settings.Secure.INSTALL_NON_MARKET_APPS, 0);
		if (result == 0)
			throw new Exception(
					"É necessário habilitar a instalação de aplicações desconhecidas(fora do google play) do seu equipamento. ");

		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setDataAndType(
				Uri.fromFile(new File(Environment.getExternalStorageDirectory() + "/download/" + apkName.toString())),
				"application/vnd.android.package-archive");

		startActivityForResult(intent, REQUEST_INSTALL);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == REQUEST_INSTALL) {
			if (isApplicationInstalled()) {
				new InfoAlert(ApplicationInstaller.this, "Informação", "Instalação concluída.").show();
			}
		} else if (requestCode == REQUEST_REINSTALL) {
			if (resultCode == 0) {
				if (!isApplicationInstalled()) {
					try {
						installApplication();
					} catch (Exception e) {
						e.printStackTrace();
						lastError = e.getMessage();
					}
				}
			}
		} else if (requestCode == REQUEST_UNINSTALL) {
		}
		showStatus();
		super.onActivityResult(requestCode, resultCode, data);
	}

	protected boolean isApplicationInstalled() {
		/*
		 * Não considera pacotes do sistema
		 */
		ArrayList<CustomPackageInformation> apps = getInstalledApps(false);
		final int max = apps.size();
		for (int i = 0; i < max; i++) {
			if (apps.get(i).appname.toString().equals(appName.toString()))
				return true;
		}

		return false;
	}

	protected class DownloadAndInstall extends AsyncTask<String, Integer, String> implements OnCancelListener,
			DialogInterface.OnClickListener {
		private ProgressDialog dialog;
		private int fileSize;
		private int fileProgress;
		private String packageName;
		private String actionType;
		public static final String NONE = "NONE";
		public static final String INSTALL = "INSTALL";
		public static final String REINSTALL = "REINSTALL";
		private boolean canceled;

		@Override
		protected void onPreExecute() {
			String PATH = Environment.getExternalStorageDirectory() + "/download/";
			File file = new File(PATH);
			if (!file.exists()) {
				file.mkdirs();
			}

			dialog = new ProgressDialog(ApplicationInstaller.this);
			dialog.setMessage("Baixando aplicação...");
			dialog.setIndeterminate(false);
			dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
			dialog.setProgress(0);
			dialog.setCancelable(false);
			dialog.setCanceledOnTouchOutside(false);
			dialog.show();
			dialog.setOnCancelListener(this);
			dialog.setButton("Cancelar", this);
			canceled = false;
		}

		@Override
		protected String doInBackground(String... params) {
			packageName = params[0];
			actionType = params[1];

			try {
				URL url = new URL(urlpath.toString());
				HttpURLConnection connection = (HttpURLConnection) url.openConnection();
				connection.setRequestMethod("GET");
				connection.setDoOutput(true);
				connection.connect();

				String PATH = Environment.getExternalStorageDirectory() + "/download/";
				File file = new File(PATH);
				if (!file.exists()) {
					file.mkdirs();
				}
				File outputFile = new File(file, apkName.toString());
				FileOutputStream fos = new FileOutputStream(outputFile);
				InputStream is = connection.getInputStream();

				List<String> values = connection.getHeaderFields().get("content-Length");
				fileSize = 0;
				if (values != null && !values.isEmpty()) {
					String sLength = (String) values.get(0);
					if (sLength != null) {
						fileSize = Integer.valueOf(sLength);
					}
				}

				byte[] buffer = new byte[1024];
				int len1 = 0;
				fileProgress = 0;
				while ((len1 = is.read(buffer)) != -1 && !canceled) {
					fileProgress = fileProgress + len1;
					publishProgress(fileProgress);
					fos.write(buffer, 0, len1);
				}
				fos.close();
				is.close();
				publishProgress(fileProgress);
				if (canceled)
					return "Operação cancelada pelo usuário.";
				return OK;
			} catch (FileNotFoundException e) {
				e.printStackTrace();
				return "Arquivo " + apkName + " não encontrado no servidor.";
			} catch (IOException e) {
				e.printStackTrace();
				return "Erro obtendo arquivo " + apkName + " no servidor. ";
			} catch (Exception e) {
				e.printStackTrace();
				return "Erro obtendo arquivo " + apkName + " no servidor. " + e.getMessage();
			}
		}

		@Override
		protected void onPostExecute(String result) {
			dialog.dismiss();
			if (!"OK".equals(result)) {
				new ErrorAlert(ApplicationInstaller.this, "Erro", result).show();
			} else {
				if (INSTALL.equals(actionType))
					try {
						installApplication();
					} catch (Exception e) {
						e.printStackTrace();
						new ErrorAlert(ApplicationInstaller.this, "Erro", e.getMessage()).show();
					}
				else if (REINSTALL.equals(actionType)) {
					try {
						installApplication();
					} catch (Exception e) {
						e.printStackTrace();
						new ErrorAlert(ApplicationInstaller.this, "Erro", e.getMessage()).show();
					}
				}

			}
		}

		@Override
		protected void onProgressUpdate(Integer... values) {
			dialog.setMessage("Baixando aplicação " + appName + "...");
			if (fileSize == 0) {
				dialog.setIndeterminate(true);
			} else {
				dialog.setMax(fileSize);
				dialog.setProgress(values[0]);
			}
		}

		private String calculateSize(double value) {

			String unit = "Bytes";
			double sizeInUnit = 0d;

			double size = value;

			if (size > 1024 * 1024 * 1024) { // Gigabyte
				sizeInUnit = size / (1024 * 1024 * 1024);
				unit = "GB";
			} else if (size > 1024 * 1024) { // Megabyte
				sizeInUnit = size / (1024 * 1024);
				unit = "MB";
			} else if (size > 1024) { // Kilobyte
				sizeInUnit = size / 1024;
				unit = "KB";
			} else { // Byte
				sizeInUnit = size;
			}

			return new DecimalFormat("###.##").format(sizeInUnit) + " " + unit;
		}

		@Override
		public void onCancel(DialogInterface dialog) {
			canceled = true;
		}

		@Override
		public void onClick(DialogInterface dialog, int which) {
			dialog.cancel();
		}

	}

	/**
	 * Obtém a versão da aplicação no servidor
	 */
	private class GetVersionFromServer extends AsyncTask<String, Void, String> {

		private Context context;
		private ProgressDialog dialog;
		private OnFinishListener finishListener;

		public GetVersionFromServer(Context context, OnFinishListener finishListener) {
			super();
			this.context = context;
			this.finishListener = finishListener;
		}

		@Override
		protected void onPreExecute() {
			dialog = new ProgressDialog(context);
			dialog.setCancelable(false);
			dialog.setCanceledOnTouchOutside(false);
			dialog.setTitle("Aguarde...");
			dialog.setMessage("Verificando a existência de novas versões.");
			dialog.show();
		}

		@Override
		protected void onPostExecute(String result) {
			dialog.dismiss();
			if (!OK.equals(result)) {
				new ErrorAlert(context, "Erro", "Não foi possível verificar a existência de novas versões: " + result)
						.show();
				if (finishListener != null)
					finishListener.onError();
			} else {
				if (finishListener != null)
					finishListener.onSucess();
			}
		}

		@Override
		protected String doInBackground(String... params) {
			URL url;
			try {
				String buildVersionPath = params[0];

				url = new URL(buildVersionPath.toString());

				HttpURLConnection connection = (HttpURLConnection) url.openConnection();
				connection.setRequestMethod("GET");
				connection.setDoOutput(true);
				connection.connect();
				InputStream in = connection.getInputStream();

				ByteArrayOutputStream baos = new ByteArrayOutputStream();

				byte[] buffer = new byte[1024];
				int len1 = 0;
				while ((len1 = in.read(buffer)) != -1) {
					baos.write(buffer, 0, len1);
				}

				String temp = "";
				String s = baos.toString();
				for (int i = 0; i < s.length(); i++) {
					i = s.indexOf("=") + 1;
					while (s.charAt(i) == ' ')
						i++;
					while (s.charAt(i) != ';' && (s.charAt(i) >= '0' && s.charAt(i) <= '9' || s.charAt(i) == '.')) {
						temp = temp.toString().concat(Character.toString(s.charAt(i)));
						i++;
					}
					s = s.substring(i);
					temp = temp + " ";

				}
				String[] fields = temp.split(" ");// divide Version Code e
													// Version
													// Name
				versionCode = Integer.parseInt(fields[0].toString());
				versionName = fields[1].toString();
				baos.close();
				return OK;
			} catch (Exception e) {
				e.printStackTrace();
				return e.getMessage() + "";
			}
		}
	}

	private interface OnFinishListener {
		void onSucess();

		void onError();
	}

}
