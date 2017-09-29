package calendarioacademico.login;

import calendarioacademico.commons.Usuario;
import calendarioacademico.utils.EManager;
import java.io.IOException;
import java.io.Serializable;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import java.util.List;
import javax.faces.context.ExternalContext;
import java.util.UUID;
import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.SimpleEmail;

@ManagedBean
@SessionScoped
public class LoginBean implements Serializable {

    private static String username;
    private String password;
    private String recuperaUsuario;
    private String recuperaEmail;
    private static String nivelAcesso = "";

    private boolean loggedIn;

    @ManagedProperty(value = "#{navigationBean}")
    private NavigationBean navigationBean;

    public String doLogin() throws IOException {
        List<Usuario> users = EManager.getInstance().createNamedQuery("Usuario.findByLoginSenha", Usuario.class).setParameter("login", username).setParameter("senha", MD5Util.md5Hash(password)).getResultList();

        // Login sucesso
        if (users.size() > 0) {
            loggedIn = true;
//            LogUtil.saveChangeLog("Fez login", users.get(0).getLogin());
            nivelAcesso = users.get(0).getNivelacesso();
            ExternalContext context = FacesContext.getCurrentInstance().getExternalContext();
            context.redirect(context.getRequestContextPath() + "/webapp/index.xhtml");
            return navigationBean.redirectToWelcome();
        }
        popupMessage_DadosIncorretos();
        return navigationBean.toLogin();
    }

    public String doLogout() throws IOException {
        loggedIn = false;

        ExternalContext context = FacesContext.getCurrentInstance().getExternalContext();
        context.redirect(context.getRequestContextPath() + "/login.xhtml");
        this.username = "";
        this.password = "";
        this.nivelAcesso = "";
        return navigationBean.toLogin();
    }

    public void doRecover() throws EmailException {
        Usuario u = null;
        try {
            u = (Usuario) EManager.getInstance().createNamedQuery("Usuario.findByLoginEmail", Usuario.class).setParameter("login", this.recuperaUsuario).setParameter("email", this.recuperaEmail).getSingleResult();
        } catch (Exception e) {
            System.out.println("Usuário não encontrado.");
        }
        if (u != null) {
            String uuid = UUID.randomUUID().toString();
            sendMail(this.recuperaEmail, uuid);
            u.setSenha(MD5Util.md5Hash(uuid));
            EManager.getInstance().getTransaction().begin();
            EManager.getInstance().merge(u);
            EManager.getInstance().getTransaction().commit();
        } else {
            popupMessage_DadosIncorretos();
        }
        this.recuperaEmail = "";
        this.recuperaUsuario = "";
    }

    private void sendMail(String destino, String uuid) throws EmailException {
        Email email = new SimpleEmail();
        email.setHostName("smtp.gmail.com");
        email.setSmtpPort(587);
        email.setAuthenticator(new DefaultAuthenticator("laptiutfpr@gmail.com", "*lapti@utfpr&2017*"));
        email.setTLS(true);
        email.setFrom("laptiutfpr@gmail.com");
        email.setSubject("Bombeiros - Recuperação de Senha");
        email.setMsg("Sua nova senha é: " + uuid);
        email.addTo(destino);
        email.send();
        popupMessage_EmailEnviado();
    }

    public void addMessage(String summary, String detail) {
        FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, summary, detail);
        FacesContext.getCurrentInstance().addMessage(null, message);
    }

    public void popupMessage_DadosIncorretos() {
        addMessage("Erro", "Dados faltantes ou incorretos.");
    }

    public void popupMessage_EmailEnviado() {
        addMessage("Recuperação", "Uma nova senha foi enviada para o e-mail cadastrado.");
    }

    public static String getUsernameStatic() {
        return username;
    }

    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isLoggedIn() {
        return loggedIn;
    }

    public void setLoggedIn(boolean loggedIn) {
        this.loggedIn = loggedIn;
    }

    public void setNavigationBean(NavigationBean navigationBean) {
        this.navigationBean = navigationBean;
    }

    public static String getNivelAcesso() {
        return nivelAcesso;
    }

    public static void setNivelAcesso(String nivelAcesso) {
        LoginBean.nivelAcesso = nivelAcesso;
    }

    public String getRecuperaUsuario() {
        return recuperaUsuario;
    }

    public void setRecuperaUsuario(String recuperaUsuario) {
        this.recuperaUsuario = recuperaUsuario;
    }

    public String getRecuperaEmail() {
        return recuperaEmail;
    }

    public void setRecuperaEmail(String recuperaEmail) {
        this.recuperaEmail = recuperaEmail;
    }
}
