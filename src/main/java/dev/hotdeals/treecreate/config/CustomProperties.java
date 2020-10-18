package dev.hotdeals.treecreate.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties("custom.property")
public class CustomProperties
{
    private String nordeaSecret;

    public String getNordeaSecret()
    {
        return nordeaSecret;
    }

    public void setNordeaSecret(String nordeaSecret)
    {
        this.nordeaSecret = nordeaSecret;
    }
}
