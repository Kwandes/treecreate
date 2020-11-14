package dev.hotdeals.treecreate.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import static org.hibernate.bytecode.BytecodeLogger.LOGGER;

@Component
@ConfigurationProperties("custom.property")
public class CustomProperties
{
    private String quickpaySecret;

    private String environmentType;
    public String getQuickpaySecret()
    {
        return quickpaySecret;
    }

    public void setQuickpaySecret(String quickpaySecret)
    {
        this.quickpaySecret = quickpaySecret;
    }

    public String getEnvironmentType()
    {
        return environmentType;
    }

    public void setEnvironmentType(String environmentType)
    {
        if (environmentType.equals("${TREECREATE_ENV}"))
            environmentType = "development";
        LOGGER.info("Environment type set to " + environmentType);
        this.environmentType = environmentType;
    }

}
