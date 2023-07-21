package ru.practicum.ewmservice.config;

import org.hibernate.boot.MetadataBuilder;
import org.hibernate.boot.spi.MetadataBuilderContributor;
import org.hibernate.dialect.function.SQLFunctionTemplate;
import org.hibernate.type.FloatType;

public class SQLFunctionContributor implements MetadataBuilderContributor {
    @Override
    public void contribute(MetadataBuilder metadataBuilder) {
        metadataBuilder.applySqlFunction("distance",
                new SQLFunctionTemplate(FloatType.INSTANCE, "DISTANCE(?1,?2,?3,?4)"));
    }
}
