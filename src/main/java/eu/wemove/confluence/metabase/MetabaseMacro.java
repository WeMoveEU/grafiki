package eu.wemove.confluence.metabase;

import java.util.Map;
import java.util.HashMap;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.macro.Macro;
import com.atlassian.confluence.macro.MacroExecutionException;
import com.atlassian.confluence.xhtml.api.XhtmlContent;
import com.atlassian.plugin.spring.scanner.annotation.component.Scanned;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;

import io.fusionauth.jwt.Signer;
import io.fusionauth.jwt.domain.JWT;
import io.fusionauth.jwt.hmac.HMACSigner;

import org.springframework.beans.factory.annotation.Autowired;

@Scanned
public class MetabaseMacro implements Macro {
  private final XhtmlContent xhtmlUtils;

  @Autowired
  public MetabaseMacro(@ComponentImport XhtmlContent xhtmlUtils) 
  {
      this.xhtmlUtils = xhtmlUtils;   
  }

  @Override
  public String execute(Map<String, String> arg0, String arg1, ConversionContext arg2) throws MacroExecutionException {
    String baseURL = "https://example.com/embed/question/";
    String secretKey = "Read the Metabase secret key from config";

    Integer questionId = 42; //Metabase question id
    Map<String, Integer> resourceClaim = new HashMap<String, Integer>();
    resourceClaim.put("question", questionId);

    String paramName1 = "some_parameter_name";
    String paramValue1 = "the parameter value";
    Map<String, String> paramsClaim = new HashMap<String, String>();
    paramsClaim.put(paramName1, paramValue1);

		Signer signer = HMACSigner.newSHA256Signer(secretKey);
		JWT jwt = new JWT().addClaim("resource", resourceClaim).addClaim("params", paramsClaim);
		String encodedJWT = JWT.getEncoder().encode(jwt, signer);

		StringBuilder iFrame = new StringBuilder();
		iFrame.append("<iframe src=\"");
		iFrame.append(baseURL);
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
