package br.com.anteros.android.spring.converter;

import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

import br.com.anteros.persistence.serialization.jackson.AnterosObjectMapper;
import br.com.anteros.persistence.session.SQLSessionFactory;

public class AnterosAndroidForceLazyHttpMessageConverter extends MappingJackson2HttpMessageConverter {
	
	public AnterosAndroidForceLazyHttpMessageConverter(SQLSessionFactory sessionFactory) {
		AnterosObjectMapper objectMapper = new AnterosObjectMapper(sessionFactory);
		this.setObjectMapper(objectMapper);
	}

}
