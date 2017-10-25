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
    private Usuario novoUsuario = new Usuario();
    private Usuario usuarioRecuperaCodigo = new Usuario();
    private String loginRecuperaCodigo = "";
    private String emailRecuperaCodigo = "";
    private String codigorecuperacao = "";
    private String novaSenhaRecuperaCodigo = "";
    private boolean admin = false;

    private boolean loggedIn;

    @ManagedProperty(value = "#{navigationBean}")
    private NavigationBean navigationBean;

    public String doLogin() throws IOException {
        List<Usuario> users = EManager.getInstance().createNamedQuery("Usuario.findByLoginSenha", Usuario.class).setParameter("login", username).setParameter("senha", MD5Util.md5Hash(password)).getResultList();

        // Login sucesso
        if (users.size() > 0) {
            loggedIn = true;
            nivelAcesso = users.get(0).getNivelacesso();
            if (nivelAcesso.equalsIgnoreCase("Administrador")) {
                this.admin = true;
            } else {
                this.admin = false;
            }
            ExternalContext context = FacesContext.getCurrentInstance().getExternalContext();
            context.redirect(context.getRequestContextPath() + "/webapp/index.xhtml");
            return navigationBean.redirectToWelcome();
        }
        popupMessage_DadosIncorretos();
        return navigationBean.toLogin();
    }

    public void doCadastro() {
        try {
            this.novoUsuario.setNivelacesso("Usuário");
            if (this.novoUsuario.getLogin().equalsIgnoreCase("") || this.novoUsuario.getLogin().length() < 4 || this.novoUsuario.getSenha().length() < 6 || this.novoUsuario.getEmail().length() < 6) {
                popupMessage_DadosIncorretos();
            } else {
                this.novoUsuario.setSenha(MD5Util.md5Hash(this.novoUsuario.getSenha()));
                EManager.getInstance().getTransaction().begin();
                EManager.getInstance().persist(this.novoUsuario);
                EManager.getInstance().getTransaction().commit();
                popupMessage_CadastroSucesso();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.novoUsuario = new Usuario();
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
            u.setCodigorecuperacao(uuid);
            EManager.getInstance().getTransaction().begin();
            EManager.getInstance().merge(u);
            EManager.getInstance().getTransaction().commit();
        } else {
            popupMessage_DadosIncorretos();
        }
        this.recuperaEmail = "";
        this.recuperaUsuario = "";
    }

    public void doRecoverCodigo() {
        Usuario recoverUser = new Usuario();
        recoverUser = (Usuario) EManager.getInstance().createNamedQuery("Usuario.findByLoginEmailCodigo").setParameter("login", this.loginRecuperaCodigo).setParameter("email", this.emailRecuperaCodigo).setParameter("codigorecuperacao", this.codigorecuperacao).getSingleResult();
        if (recoverUser.getId() != null) {
            recoverUser.setSenha(MD5Util.md5Hash(this.novaSenhaRecuperaCodigo));
            recoverUser.setCodigorecuperacao(null);
            EManager.getInstance().getTransaction().begin();
            EManager.getInstance().merge(recoverUser);
            EManager.getInstance().getTransaction().commit();
            popupMessage_ResetSenhaSucesso();
        } else {
            popupMessage_DadosIncorretos();
        }
    }

    private void sendMail(String destino, String uuid) throws EmailException {
        Email email = new SimpleEmail();
        email.setHostName("smtp.gmail.com");
        email.setSmtpPort(587);
        email.setAuthenticator(new DefaultAuthenticator("calendarioeventosbsi@gmail.com", "disciplinabsi"));
        email.setTLS(true);
        email.setFrom("calendarioeventosbsi@gmail.com");
        email.setSubject("Calendário de Eventos - Recuperação de senha");
        email.setMsg("Seu código é: " + uuid);
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

    public void popupMessage_CadastroSucesso() {
        addMessage("Sucesso", "Cadastro concluído.");
    }

    public void popupMessage_EmailEnviado() {
        addMessage("Recuperação", "Uma nova senha foi enviada para o e-mail cadastrado.");
    }

    public void popupMessage_ResetSenhaSucesso() {
        addMessage("Recuperação", "Login atualizado com sucesso.");
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

    public Usuario getNovoUsuario() {
        return novoUsuario;
    }

    public void setNovoUsuario(Usuario novoUsuario) {
        this.novoUsuario = novoUsuario;
    }

    public Usuario getUsuarioRecuperaCodigo() {
        return usuarioRecuperaCodigo;
    }

    public void setUsuarioRecuperaCodigo(Usuario usuarioRecuperaCodigo) {
        this.usuarioRecuperaCodigo = usuarioRecuperaCodigo;
    }

    public String getLoginRecuperaCodigo() {
        return loginRecuperaCodigo;
    }

    public void setLoginRecuperaCodigo(String loginRecuperaCodigo) {
        this.loginRecuperaCodigo = loginRecuperaCodigo;
    }

    public String getEmailRecuperaCodigo() {
        return emailRecuperaCodigo;
    }

    public void setEmailRecuperaCodigo(String emailRecuperaCodigo) {
        this.emailRecuperaCodigo = emailRecuperaCodigo;
    }

    public String getCodigorecuperacao() {
        return codigorecuperacao;
    }

    public void setCodigorecuperacao(String codigorecuperacao) {
        this.codigorecuperacao = codigorecuperacao;
    }

    public String getNovaSenhaRecuperaCodigo() {
        return novaSenhaRecuperaCodigo;
    }

    public void setNovaSenhaRecuperaCodigo(String novaSenhaRecuperaCodigo) {
        this.novaSenhaRecuperaCodigo = novaSenhaRecuperaCodigo;
    }

    public boolean isAdmin() {
        return admin;
    }

    public void setAdmin(boolean admin) {
        this.admin = admin;
    }

}
