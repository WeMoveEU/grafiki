package eu.wemove.confluence.grafiki;

import java.net.URI;
import java.io.IOException;

import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.atlassian.plugin.spring.scanner.annotation.component.Scanned;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.sal.api.auth.LoginUriProvider;
import com.atlassian.sal.api.user.UserManager;
import com.atlassian.templaterenderer.TemplateRenderer;

@Scanned
public class AdminServlet extends HttpServlet
{
  @ComponentImport
  private final UserManager userManager;
  @ComponentImport
  private final LoginUriProvider loginUriProvider;
  @ComponentImport
  private final TemplateRenderer renderer;

  @Inject
  public AdminServlet(UserManager userManager, LoginUriProvider loginUriProvider, TemplateRenderer templateRenderer)
  {
    this.userManager = userManager;
    this.loginUriProvider = loginUriProvider;
    this.renderer = templateRenderer;
  }

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException
  {
    String username = userManager.getRemoteUsername(request);
    if (username == null || !userManager.isSystemAdmin(username))
    {
      this.redirectToLogin(request, response);
      return;
    }

    response.setContentType("text/html;charset=utf-8");
    this.renderer.render("admin.vm", response.getWriter());
  }

	private void redirectToLogin(HttpServletRequest request, HttpServletResponse response) throws IOException
	{
		response.sendRedirect(this.loginUriProvider.getLoginUri(getUri(request)).toASCIIString());
	}
	 
	private URI getUri(HttpServletRequest request)
	{
		StringBuffer builder = request.getRequestURL();
		if (request.getQueryString() != null)
		{
			builder.append("?");
			builder.append(request.getQueryString());
		}
		return URI.create(builder.toString());
	}
}
