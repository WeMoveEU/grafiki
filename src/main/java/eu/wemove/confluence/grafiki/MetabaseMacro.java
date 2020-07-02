package eu.wemove.confluence.grafiki;

import java.util.Map;
import java.util.HashMap;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.macro.Macro;
import com.atlassian.confluence.macro.MacroExecutionException;
import com.atlassian.confluence.xhtml.api.XhtmlContent;
import com.atlassian.plugin.spring.scanner.annotation.component.Scanned;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.sal.api.pluginsettings.PluginSettings;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;

import io.fusionauth.jwt.Signer;
import io.fusionauth.jwt.domain.JWT;
import io.fusionauth.jwt.hmac.HMACSigner;

import org.springframework.beans.factory.annotation.Autowired;

@Scanned
public class MetabaseMacro implements Macro {
  private final XhtmlContent xhtmlUtils;
  private final PluginSettingsFactory pluginSettingsFactory;

  @Autowired
  public MetabaseMacro(@ComponentImport XhtmlContent xhtmlUtils, @ComponentImport PluginSettingsFactory pluginSettingsFactory) 
  {
      this.xhtmlUtils = xhtmlUtils;   
      this.pluginSettingsFactory = pluginSettingsFactory;
  }

  @Override
  public String execute(Map<String, String> params, String arg1, ConversionContext arg2) throws MacroExecutionException {
    PluginSettings settings = this.pluginSettingsFactory.createGlobalSettings();
    String baseURL = (String) settings.get("grafiki.metabase.baseUrl");
    String secretKey = (String) settings.get("grafiki.metabase.secretKey");

    String questionUrl = params.get("question");
    Pattern questionPattern = Pattern.compile(baseURL + "/question/([0-9]+).*");
    Matcher m = questionPattern.matcher(questionUrl);
    if (!m.matches()) {
      throw new MacroExecutionException("Invalid question URL");
    }

    Integer questionId = Integer.parseInt(m.group(1));
    Map<String, Integer> resourceClaim = new HashMap<String, Integer>();
    resourceClaim.put("question", questionId);

    Map<String, String> paramsClaim = new HashMap<String, String>();

		Signer signer = HMACSigner.newSHA256Signer(secretKey);
		JWT jwt = new JWT().addClaim("resource", resourceClaim).addClaim("params", paramsClaim);
		String encodedJWT = JWT.getEncoder().encode(jwt, signer);

		StringBuilder iFrame = new StringBuilder();
		iFrame.append("<iframe src=\"");
		iFrame.append(baseURL + "/embed/question/");
    iFrame.append(encodedJWT);
    iFrame.append("\" frameborder=0 width=\"600\" height=\"300\"></iframe>");
		return iFrame.toString();
  }

  @Override
  public BodyType getBodyType() {
      return BodyType.NONE;
  }

  @Override
  public OutputType getOutputType() {
      return OutputType.BLOCK;
  }
}
