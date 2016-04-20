package br.com.anteros.android.spring.converter;

import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

import br.com.anteros.persistence.serialization.jackson.AnterosObjectMapper;
import br.com.anteros.persistence.session.SQLSessionFactory;

public class AnterosAndroidHttpMessageConverter extends MappingJackson2HttpMessageConverter {

	public AnterosAndroidHttpMessageConverter(SQLSessionFactory sessionFactory) {
		this.setObjectMapper(new AnterosObjectMapper(sessionFactory));
	}

}
