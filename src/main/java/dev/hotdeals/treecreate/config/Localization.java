package dev.hotdeals.treecreate.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.CookieLocaleResolver;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;

import java.util.Locale;

@EnableConfigurationProperties(CustomProperties.class) // used to auto-initialize the property class. Not used in this class
@Configuration
public class Localization implements WebMvcConfigurer
{

    @Bean
    public LocaleResolver localeResolver()
    {
        CookieLocaleResolver slr = new CookieLocaleResolver();
        slr.setCookieName("Language-Locale");
        slr.setDefaultLocale(new Locale("dk"));
        //slr.setDefaultLocale(Locale.ENGLISH);
        return slr;
    }

    @Bean
    public LocaleChangeInterceptor localeChangeInterceptor()
    {
        LocaleChangeInterceptor lci = new LocaleChangeInterceptor();
        lci.setParamName("lang");
        return lci;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry)
    {
        registry.addInterceptor(localeChangeInterceptor());
    }
}
