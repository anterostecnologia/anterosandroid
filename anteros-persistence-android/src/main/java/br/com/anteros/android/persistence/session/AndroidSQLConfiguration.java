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

package br.com.anteros.android.persistence.session;

import java.io.InputStream;
import java.util.List;

import javax.sql.DataSource;

import org.xmlpull.v1.XmlPullParser;

import android.content.Context;
import android.util.Xml;
import br.com.anteros.android.core.util.AndroidClassPathScanner;
import br.com.anteros.core.scanner.ClassFilter;
import br.com.anteros.core.utils.ObjectUtils;
import br.com.anteros.core.utils.StringUtils;
import br.com.anteros.persistence.metadata.accessor.PropertyAccessorFactory;
import br.com.anteros.persistence.metadata.annotation.Converter;
import br.com.anteros.persistence.metadata.annotation.Entity;
import br.com.anteros.persistence.metadata.annotation.EnumValues;
import br.com.anteros.persistence.metadata.configuration.PersistenceModelConfiguration;
import br.com.anteros.persistence.session.SQLSessionFactory;
import br.com.anteros.persistence.session.configuration.AbstractPersistenceConfiguration;
import br.com.anteros.persistence.session.configuration.AnterosPersistenceConfigurationBase;
import br.com.anteros.persistence.session.configuration.DataSourceConfiguration;
import br.com.anteros.persistence.session.configuration.DataSourcesConfiguration;
import br.com.anteros.persistence.session.configuration.PackageScanEntity;
import br.com.anteros.persistence.session.configuration.PlaceholderConfiguration;
import br.com.anteros.persistence.session.configuration.PropertyConfiguration;
import br.com.anteros.persistence.session.configuration.SessionFactoryConfiguration;
import br.com.anteros.persistence.session.configuration.exception.AnterosConfigurationException;
import br.com.anteros.persistence.session.exception.SQLSessionFactoryException;

public class AndroidSQLConfiguration extends AnterosPersistenceConfigurationBase {

	private static final String SESSION_FACTORY = "session-factory";
	public static final String PROPERTIES = "properties";
	public static final String DATA_SOURCES = "dataSources";
	protected Context context;

	public AndroidSQLConfiguration(Context context) {
		super();
		this.context = context;
	}

	public AndroidSQLConfiguration(Context context, PersistenceModelConfiguration modelConfiguration) {
		super(modelConfiguration);
		this.context = context;
	}

	public AndroidSQLConfiguration() throws Exception {
		super();
	}

	public AndroidSQLConfiguration(DataSource dataSource) throws Exception {
		super(dataSource);
	}

	public AndroidSQLConfiguration(PersistenceModelConfiguration modelConfiguration) throws Exception {
		super(modelConfiguration);
	}

	public AndroidSQLConfiguration(DataSource dataSource, PersistenceModelConfiguration modelConfiguration)
			throws Exception {
		super(dataSource, modelConfiguration);
	}

	@Override
	public AbstractPersistenceConfiguration configure() throws AnterosConfigurationException {
		throw new AnterosConfigurationException(
				"Não é permitido usar este método no Android. Use os métodos configure() com InputStream.");
	}

	@Override
	public AbstractPersistenceConfiguration configure(String xmlFile) throws AnterosConfigurationException {
		throw new AnterosConfigurationException(
				"Não é permitido usar este método no Android. Use os métodos configure() com InputStream.");
	}

	protected void prepareClassesToLoad() throws ClassNotFoundException {
		LOG.debug("Preparando classes para ler entidades.");
		if ((getSessionFactoryConfiguration().getPackageToScanEntity() != null)
				&& (!"".equals(getSessionFactoryConfiguration().getPackageToScanEntity().getPackageName()))) {
			if (getSessionFactoryConfiguration().isIncludeSecurityModel())
				getSessionFactoryConfiguration().getPackageToScanEntity().setPackageName(
						getSessionFactoryConfiguration().getPackageToScanEntity().getPackageName() + ", "
								+ SECURITY_PACKAGE);
			String[] packages = StringUtils.tokenizeToStringArray(getSessionFactoryConfiguration()
					.getPackageToScanEntity().getPackageName(), ", ;");
			List<Class<?>> scanClasses = AndroidClassPathScanner.scanClasses(context,
					new ClassFilter().packages(packages).annotation(Entity.class).annotation(Converter.class)
							.annotation(EnumValues.class).packageName(CONVERTERS_PACKAGE));
			if (LOG.isDebugEnabled()) {
				for (Class<?> cl : scanClasses) {
					LOG.debug("Encontrado classe scaneada " + cl.getName());
				}
			}
			getSessionFactoryConfiguration().addToAnnotatedClasses(scanClasses);
		}

		if ((getSessionFactoryConfiguration().getClasses() == null)
				|| (getSessionFactoryConfiguration().getClasses().size() == 0))
			throw new SQLSessionFactoryException(
					"Não foram encontradas classes representando entidades. Informe o pacote onde elas podem ser localizadas ou informe manualmente cada uma delas.");

		LOG.debug("Preparação das classes concluída.");
	}

	@Override
	public SQLSessionFactory buildSessionFactory() throws Exception {
		prepareClassesToLoad();
		buildDataSource();
		AndroidSQLSessionFactory sessionFactory = new AndroidSQLSessionFactory(context, entityCacheManager, dataSource,
				this.getSessionFactoryConfiguration());
		loadEntities(sessionFactory.getDialect());
		sessionFactory.generateDDL();
		return sessionFactory;
	}

	@Override
	protected void buildDataSource() throws Exception {
	}

	public Context getContext() {
		return context;
	}

	public AndroidSQLConfiguration context(Context context) {
		this.context = context;
		return this;
	}

	@Override
	public AbstractPersistenceConfiguration configure(InputStream xmlConfiguration)
			throws AnterosConfigurationException {
		if (context == null)
			throw new AnterosConfigurationException(
					"Não é possível configurar a persistencia sem que seja informado o contexto do Android.");
		return super.configure(xmlConfiguration);
	}

	@Override
	public AbstractPersistenceConfiguration configure(InputStream xmlConfiguration, InputStream placeHolder)
			throws AnterosConfigurationException {
		if (context == null)
			throw new AnterosConfigurationException(
					"Não é possível configurar a persistencia sem que seja informado o contexto do Android.");
		return super.configure(xmlConfiguration, placeHolder);
	}

	@Override
	public PropertyAccessorFactory getPropertyAccessorFactory() {
		return null;
	}

	@Override
	protected AbstractPersistenceConfiguration parseXmlConfiguration(InputStream xmlConfiguration) throws Exception {

		XmlPullParser parser = Xml.newPullParser();
		parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
		parser.setInput(xmlConfiguration, null);
		parser.nextTag();

		int eventType = parser.getEventType();

		SessionFactoryConfiguration sessionFactoryConfiguration = new SessionFactoryConfiguration();
		DataSourceConfiguration dataSourceConfiguration = null;

		String previousTag = "";

		while (eventType != XmlPullParser.END_DOCUMENT) {
			String name = null;
			switch (eventType) {
			case XmlPullParser.START_DOCUMENT:
				break;
			case XmlPullParser.START_TAG:
				name = parser.getName();
				if (SESSION_FACTORY.equalsIgnoreCase(name)) {

				} else if (PLACEHOLDER.equalsIgnoreCase(name)) {
					sessionFactoryConfiguration
							.setPlaceholder(new PlaceholderConfiguration(parser.getAttributeValue(0)));
				} else if (PACKAGE_SCAN_ENTITY.equalsIgnoreCase(name)) {
					sessionFactoryConfiguration.setPackageToScanEntity(new PackageScanEntity(parser
							.getAttributeValue(0)));
				} else if (INCLUDE_SECURITY_MODEL.equalsIgnoreCase(name)) {
					//parser.nextToken();
					sessionFactoryConfiguration.setIncludeSecurityModel((Boolean) ObjectUtils.convert(parser.nextText(),
							Boolean.class));
				} else if (DATA_SOURCES.equalsIgnoreCase(name)) {
					DataSourcesConfiguration dataSourcesConfiguration = new DataSourcesConfiguration();
					sessionFactoryConfiguration.setDataSources(dataSourcesConfiguration);
				} else if (DATA_SOURCE.equalsIgnoreCase(name)) {
					previousTag = DATA_SOURCE;
					dataSourceConfiguration = new DataSourceConfiguration("", "");
					for (int i = 0; i < parser.getAttributeCount(); i++) {
						if (parser.getAttributeName(i).equalsIgnoreCase(ID)) {
							dataSourceConfiguration.setId(parser.getAttributeValue(i));
						} else if (parser.getAttributeName(i).equalsIgnoreCase(CLASS_NAME)) {
							dataSourceConfiguration.setClazz(parser.getAttributeValue(i));
						}
					}
					sessionFactoryConfiguration.getDataSources().getDataSources().add(dataSourceConfiguration);
				} else if (PROPERTIES.equalsIgnoreCase(name)) {
					previousTag = PROPERTIES;
				} else if (PROPERTY.equalsIgnoreCase(name) && previousTag.equals(DATA_SOURCE)) {
					PropertyConfiguration property = new PropertyConfiguration();
					for (int i = 0; i < parser.getAttributeCount(); i++) {
						if (parser.getAttributeName(i).equalsIgnoreCase(NAME)) {
							property.setName(parser.getAttributeValue(i));
						} else if (parser.getAttributeName(i).equalsIgnoreCase(VALUE)) {
							property.setValue(parser.getAttributeValue(i));
						}
					}
					dataSourceConfiguration.getProperties().add(property);
				} else if (PROPERTY.equalsIgnoreCase(name) && previousTag.equals(PROPERTIES)) {
					String _name = "";
					String _value = "";
					for (int i = 0; i < parser.getAttributeCount(); i++) {
						if (parser.getAttributeName(i).equalsIgnoreCase(NAME)) {
							_name = parser.getAttributeValue(i);
						} else if (parser.getAttributeName(i).equalsIgnoreCase(VALUE)) {
							_value = parser.getAttributeValue(i);
						}
					}
					sessionFactoryConfiguration.addProperty(_name, _value);
				} else if (CLASS_NAME.equalsIgnoreCase(name)) {
					//parser.nextToken();
					sessionFactoryConfiguration.addAnnotatedClass(parser.nextText());
				}

				break;
			case XmlPullParser.END_TAG:
				break;
			}
			eventType = parser.next();
		}

		setSessionFactory(sessionFactoryConfiguration);

		return this;
	}

	public AndroidSQLConfiguration setPackageToScanEntity(PackageScanEntity packageToScanEntity){
		getSessionFactoryConfiguration().setPackageToScanEntity(packageToScanEntity);
		return this;
	}

}
