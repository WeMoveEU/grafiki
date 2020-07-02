package eu.wemove.confluence.grafiki;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.atlassian.plugin.spring.scanner.annotation.component.Scanned;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import javax.inject.Inject;

import com.atlassian.sal.api.pluginsettings.PluginSettings;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import com.atlassian.sal.api.transaction.TransactionCallback;
import com.atlassian.sal.api.transaction.TransactionTemplate;
import com.atlassian.sal.api.user.UserManager;

@Path("/")
@Scanned
public class ConfigResource
{
    @ComponentImport
    private final UserManager userManager;
    @ComponentImport
    private final PluginSettingsFactory pluginSettingsFactory;
    @ComponentImport
    private final TransactionTemplate transactionTemplate;

    @Inject
    public ConfigResource(UserManager userManager, PluginSettingsFactory pluginSettingsFactory,
                          TransactionTemplate transactionTemplate)
    {
        this.userManager = userManager;
        this.pluginSettingsFactory = pluginSettingsFactory;
        this.transactionTemplate = transactionTemplate;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response get(@Context HttpServletRequest request)
    {
      String username = userManager.getRemoteUsername(request);
      if (username == null || !userManager.isSystemAdmin(username))
      {
        return Response.status(Status.UNAUTHORIZED).build();
      }

      return Response.ok(transactionTemplate.execute(new TransactionCallback()
      {
        public Object doInTransaction()
        {
          PluginSettings settings = pluginSettingsFactory.createGlobalSettings();
          Config config = new Config();
          config.setBaseUrl((String) settings.get("grafiki.metabase.baseUrl"));
          config.setSecretKey((String) settings.get("grafiki.metabase.secretKey"));
          return config;
        }
      })).build();
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    public Response put(final Config config, @Context HttpServletRequest request)
    {
      String username = userManager.getRemoteUsername(request);
      if (username == null || !userManager.isSystemAdmin(username))
      {
        return Response.status(Status.UNAUTHORIZED).build();
      }

      transactionTemplate.execute(new TransactionCallback()
      {
        public Object doInTransaction()
        {
          PluginSettings pluginSettings = pluginSettingsFactory.createGlobalSettings();
          pluginSettings.put("grafiki.metabase.baseUrl", config.getBaseUrl());
          pluginSettings.put("grafiki.metabase.secretKey", config.getSecretKey());
          return null;
        }
      });
      return Response.noContent().build();
    }

    @XmlRootElement
    @XmlAccessorType(XmlAccessType.FIELD)
    public static final class Config
    {
      @XmlElement private String baseUrl;
      @XmlElement private String secretKey;
            
      public String getBaseUrl()
      {
        return baseUrl;
      }
            
      public void setBaseUrl(String baseUrl)
      {
        this.baseUrl = baseUrl;
      }
            
      public String getSecretKey()
      {
        return secretKey;
      }
            
      public void setSecretKey(String secretKey)
      {
        this.secretKey = secretKey;
      }
    }
}
