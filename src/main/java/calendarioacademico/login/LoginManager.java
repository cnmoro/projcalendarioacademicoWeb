package calendarioacademico.login;

import java.io.IOException;
import java.io.Serializable;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import java.util.List;
import javax.faces.context.ExternalContext;
import java.util.UUID;
import javax.faces.bean.ManagedProperty;
import models.Usuario;
import org.apache.commons.mail.EmailException;
import utils.EManager;
import utils.MD5Util;

@ManagedBean
@SessionScoped
public class LoginManager implements Serializable {

    private static Usuario usuarioAtual = new Usuario();
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
        List<Usuario> users = EManager.getInstance().getDatabaseAccessor().getUsuariosByLoginSenha(username, password);

        // Login sucesso
        if (users.size() > 0) {
            System.out.println("Login sucesso.");
            loggedIn = true;
            nivelAcesso = users.get(0).getNivelacesso();
            if (nivelAcesso.equalsIgnoreCase("Administrador")) {
                this.admin = true;
            } else {
                this.admin = false;
            }
            this.usuarioAtual = users.get(0);
            System.out.println("Logado como: " + this.usuarioAtual.getLogin());
            ExternalContext context = FacesContext.getCurrentInstance().getExternalContext();
            context.redirect(context.getRequestContextPath() + "/webapp/index.xhtml");
            return navigationBean.redirectToWelcome();
        } else {
            popupMessage_DadosIncorretos();
            return navigationBean.toLogin();
        }
    }

    public void doCadastro() {
        try {
            this.novoUsuario.setNivelacesso("Usuário");
            if (this.novoUsuario.getLogin().equalsIgnoreCase("") || this.novoUsuario.getLogin().length() < 4 || this.novoUsuario.getSenha().length() < 6 || this.novoUsuario.getEmail().length() < 6) {
                popupMessage_DadosIncorretos();
            } else {
                this.novoUsuario.setSenha(MD5Util.md5Hash(this.novoUsuario.getSenha()));
                EManager.getInstance().getDatabaseAccessor().cadastraUsuario(this.novoUsuario);
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
            u = EManager.getInstance().getDatabaseAccessor().getUsuarioByLoginEmail(this.recuperaUsuario, this.recuperaEmail);
        } catch (Exception e) {
            System.out.println("Usuário não encontrado.");
        }
        if (u != null) {
            String uuid = UUID.randomUUID().toString();
            EManager.getInstance().getEnviaEmailAccessor().enviaEmailRecuperacao(this.recuperaEmail, uuid);
            popupMessage_EmailEnviado();
            u.setCodigorecuperacao(uuid);
            EManager.getInstance().getDatabaseAccessor().updateUsuario(u);
        } else {
            popupMessage_DadosIncorretos();
        }
        this.recuperaEmail = "";
        this.recuperaUsuario = "";
    }

    public void doRecoverCodigo() {
        Usuario recoverUser = new Usuario();
        recoverUser = EManager.getInstance().getDatabaseAccessor().getUsuarioByLoginEmailCodigo(this.loginRecuperaCodigo, this.emailRecuperaCodigo, this.codigorecuperacao);
        if (recoverUser.getId() != null) {
            recoverUser.setSenha(MD5Util.md5Hash(this.novaSenhaRecuperaCodigo));
            recoverUser.setCodigorecuperacao(null);
            EManager.getInstance().getDatabaseAccessor().updateUsuario(recoverUser);
            popupMessage_ResetSenhaSucesso();
        } else {
            popupMessage_DadosIncorretos();
        }
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

    public static String getNivelAcesso() {
        return nivelAcesso;
    }

    public static void setNivelAcesso(String nivelAcesso) {
        LoginManager.nivelAcesso = nivelAcesso;
    }
    
    public static Usuario getUsuarioAtual() {
        return usuarioAtual;
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

    public NavigationBean getNavigationBean() {
        return navigationBean;
    }

    public void setNavigationBean(NavigationBean navigationBean) {
        this.navigationBean = navigationBean;
    }
}
