package es.uji.ei1027.toopots;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.Formatter;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.sql.DataSource;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

@Configuration
public class TooPotsConfiguration implements WebMvcConfigurer {

    // Configura l'accés a la base de dades (DataSource)
    // a partir de les propietats a src/main/resources/applications.properties
    // que comencen pel prefix spring.datasource
    @Bean
    @ConfigurationProperties(prefix = "spring.datasource")
    public DataSource dataSource() {
        return DataSourceBuilder.create().build();
}

    @Bean
    public Formatter<LocalDate> localDateFormatter() {
        return new Formatter<LocalDate>() {
            @Override
            public LocalDate parse(String text, Locale locale) throws ParseException {
                return LocalDate.parse(text, DateTimeFormatter.ISO_LOCAL_DATE);
            }

            @Override
            public String print(LocalDate object, Locale locale) {
                return DateTimeFormatter.ISO_LOCAL_DATE.format(object);
            }
        };
    }

    @Bean
    public Formatter<LocalTime> localTimeFormatter() {
        return new Formatter<LocalTime>() {
            @Override
            public LocalTime parse(String text, Locale locale) throws ParseException {
                return LocalTime.parse(text, DateTimeFormatter.ISO_LOCAL_TIME);
            }

            @Override
            public String print(LocalTime object, Locale locale) {
                return DateTimeFormatter.ISO_LOCAL_TIME.format(object);
            }
        };
    }
    
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/certificates/**").addResourceLocations("file:///C:/Users/usuario/Desktop/Escritorio/3º informatica/uji/EI1027_Disseny_impl_SI/Projecte/TooPots/src/main/resources/static/certificates").setCachePeriod(0);
        registry.addResourceHandler("/images/**").addResourceLocations("file:///C:/Users/usuario/Desktop/Escritorio/3º informatica/uji/EI1027_Disseny_impl_SI/Projecte/TooPots/src/main/resources/static/images").setCachePeriod(0);
    }
}