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

package br.com.anteros.android.persistence.backup;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;

import br.com.anteros.android.persistence.R;
import br.com.anteros.android.persistence.session.AndroidSQLSessionFactory;
import br.com.anteros.persistence.schema.type.TableCreationType;
import br.com.anteros.persistence.session.SQLSession;

public abstract class RecreateDatabaseTask extends AsyncTask<Void, Void, String> {

    private final SQLSession session;
    private ProgressDialog progress;
    private Activity activity;

    public RecreateDatabaseTask(Activity activity, SQLSession session) {
        this.activity = activity;
        this.session = session;
    }

    @Override
    protected void onPreExecute() {
        progress = ProgressDialog.show(activity, activity.getResources()
                .getString(R.string.app_name), "Aguarde...", true);
        progress.setCancelable(false);
        progress.setCanceledOnTouchOutside(false);
    }

    @Override
    protected String doInBackground(Void... params) {
        try {
            ((AndroidSQLSessionFactory)session.getSQLSessionFactory()).generateDDL(TableCreationType.DROP,
                    TableCreationType.NONE, false);
        } catch (Exception e) {
            e.printStackTrace();
            return e.getMessage() + "";
        }
        return null;
    }

    @Override
    protected void onPostExecute(String result) {
        progress.dismiss();
        if (result == null) {
            onSuccess();
        } else {
            onError("Ocorreu um erro ao recriar o banco de dados: " + result);
        }
    }

    public abstract void onSuccess();

    public abstract void onError(String message);

}
