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

package br.com.anteros.android.synchronism.communication.protocol;

import java.util.ArrayList;
import java.util.List;

import br.com.anteros.core.log.Logger;
import br.com.anteros.core.log.LoggerProvider;

public class MobileAction {

	private static Logger LOG = LoggerProvider.getInstance().getLogger(MobileAction.class.getName());

	private String name;
	private List<String[]> parameters = new ArrayList<String[]>();
	private String tableNameMobile = "";

	public MobileAction() {
	}

	public void setFormatedHeader(String header) {
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public MobileAction(String name) {
		this.name = name;
	}

	public List<String[]> getParameters() {
		return parameters;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public List<String[]> getFormatedParameters() {
		List vector = new ArrayList();
		String[] fields;
		if (parameters.size() > 0) {
			for (int i = 0; i < parameters.size(); i++) {
				fields = new String[((String[]) parameters.get(i)).length + 1];
				fields[0] = this.getName();
				for (int j = 1; j < fields.length; j++) {
					String s = ((String[]) parameters.get(i))[j - 1];
					if ((s == null) || (s.equals("")))
						fields[j] = "_";
					else
						fields[j] = ((String[]) parameters.get(i))[j - 1];
				}
				vector.add(fields);
			}
		} else {
			fields = new String[1];
			fields[0] = this.getName();
			vector.add(fields);
		}
		fields = null;
		return vector;
	}

	public void addParameter(String[] parameter) {
		this.parameters.add(parameter);
	}

	public void showDetails() {

		List<String[]> v = this.getFormatedParameters();
		LOG.info("------------------------");
		LOG.info("Action= " + this.getName());
		LOG.info("-----------------------------------------------");
		for (int i = 0; i < v.size(); i++) {
			String[] s = (String[]) v.get(i);
			for (int j = 1; j < s.length; j++) {
				LOG.info(s[j] + "|");
			}
			LOG.info("");
		}
		LOG.info("");

	}

	/**
	 * @return the tableNameMobile
	 */
	public String getTableNameMobile() {
		return tableNameMobile;
	}

	/**
	 * @param tableNameMobile
	 *            the tableNameMobile to set
	 */
	public void setTableNameMobile(String tableNameMobile) {
		this.tableNameMobile = tableNameMobile;
	}
}
