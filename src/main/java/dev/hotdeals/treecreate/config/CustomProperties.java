package dev.hotdeals.treecreate.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties("custom.property")
public class CustomProperties
{
    private String quickpaySecret;

    public String getQuickpaySecret()
    {
        return quickpaySecret;
    }

    public void setQuickpaySecret(String quickpaySecret)
    {
        this.quickpaySecret = quickpaySecret;
    }
}
