package calendarioacademico.login;

import calendarioacademico.commons.Usuario;
import calendarioacademico.utils.EManager;
import java.io.Serializable;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

/**
 *
 * @author cnmoro
 */
@ManagedBean
@ViewScoped
public class UsuarioManager implements Serializable {

    private List<Usuario> usuarios;
//    private Usuario selectedUsuario;
    private Usuario novoUsuario = new Usuario();
    private Map<String, String> nivelacesso = new HashMap<>();
    private String nivelacessoAux = "";
    private boolean permitirExclusao = true;

    private int tempUserId;
    private String tempUserLogin;
    private String tempUserSenha;
    private String tempUserEmail;
    private String tempUserNivelAcesso;

    private boolean desabilitarMudancaNivelAcesso = false;

    String regex = "^(.+)@(.+)$";
    Pattern pattern = Pattern.compile(regex);

    @PostConstruct
    public void init() {
        nivelacesso = new HashMap<>();
        nivelacesso.put("Administrador", "Administrador");
        nivelacesso.put("Colaborador Semana Acadêmica", "Colaborador Semana Acadêmica");
        nivelacesso.put("Usuário", "Usuário");
        nivelacesso.put("Professor", "Professor");
        atualizaUsuarios();
    }

    public void passa(Usuario u) {
        this.tempUserId = u.getId();
        this.tempUserLogin = u.getLogin();
        this.tempUserSenha = u.getSenha();
        this.tempUserEmail = u.getEmail();
        this.tempUserNivelAcesso = u.getNivelacesso();
        this.nivelacessoAux = u.getNivelacesso();

        if (LoginBean.getNivelAcesso().equalsIgnoreCase("Administrador")) {
            this.permitirExclusao = true;
        } else {
            this.permitirExclusao = false;
        }
    }

    public void atualizaUsuarios() {
        if (LoginBean.getNivelAcesso().equalsIgnoreCase("Administrador")) {
            this.usuarios = EManager.getInstance().createNamedQuery("Usuario.findAll").getResultList();
            this.desabilitarMudancaNivelAcesso = false;
        } else {
            this.usuarios = EManager.getInstance().createNamedQuery("Usuario.findByLogin").setParameter("login", LoginBean.getUsernameStatic()).getResultList();
            this.desabilitarMudancaNivelAcesso = true;
        }
    }

    public void modificaSenha() {
        if ((!this.tempUserSenha.equalsIgnoreCase("")) && (this.tempUserSenha.length() >= 6)) {
            this.tempUserSenha = MD5Util.md5Hash(this.tempUserSenha);
            Usuario usuario = new Usuario(this.tempUserId, this.tempUserLogin, this.tempUserSenha, this.tempUserEmail, this.tempUserNivelAcesso);
            EManager.getInstance().getTransaction().begin();
            EManager.getInstance().merge(usuario);
            EManager.getInstance().getTransaction().commit();
//                LogUtil.saveChangeLog("Modificou Senha", usuario.getLogin());
            popupMessage_SucessoModificaSenha();
        } else {
            popupMessage_ErroSenha();
        }
        atualizaUsuarios();
    }

    public void modificaUsuario() throws NoSuchAlgorithmException {
        Matcher matcher = pattern.matcher(this.tempUserEmail);
        if (matcher.matches() && this.tempUserLogin.length() <= 60 && !this.nivelacessoAux.equalsIgnoreCase("Selecione")) {

            this.tempUserNivelAcesso = this.nivelacessoAux;

//            this.tempUserSenha = MD5Util.md5Hash(this.tempUserSenha);
            Usuario usuario = new Usuario(this.tempUserId, this.tempUserLogin, this.tempUserSenha, this.tempUserEmail, this.tempUserNivelAcesso);

            EManager.getInstance().getTransaction().begin();
            EManager.getInstance().merge(usuario);
            EManager.getInstance().getTransaction().commit();
            popupMessage_SucessoModifica();
        } else {
            popupMessage_ErroDados();
        }
        this.nivelacessoAux = "";
        atualizaUsuarios();
    }

    public void deleteUsuario() {
        EManager.getInstance().getTransaction().begin();
        EManager.getInstance().remove((Usuario) EManager.getInstance().createNamedQuery("Usuario.findById").setParameter("id", this.tempUserId).getSingleResult());
        EManager.getInstance().getTransaction().commit();
//            LogUtil.saveChangeLog("Excluiu Usuário", this.tempUserLogin);
        popupMessage_SucessoDeleta();
        this.nivelacessoAux = "";
        atualizaUsuarios();
    }

    public void inserirUsuario() throws NoSuchAlgorithmException {
        Matcher matcher = pattern.matcher(this.novoUsuario.getEmail());

        if (this.novoUsuario.getSenha().length() < 6) {
            popupMessage_ErroSenha();
        } else if (matcher.matches() && this.novoUsuario.getLogin().length() <= 60 && !this.nivelacessoAux.equalsIgnoreCase("Selecione")) {
            this.novoUsuario.setNivelacesso(nivelacessoAux);

            this.novoUsuario.setSenha(MD5Util.md5Hash(this.novoUsuario.getSenha()));
            EManager.getInstance().getTransaction().begin();
            EManager.getInstance().persist(this.novoUsuario);
            EManager.getInstance().getTransaction().commit();
//                LogUtil.saveChangeLog("Criou Usuário", this.novoUsuario.getLogin());
            this.novoUsuario = new Usuario();
            popupMessage_SucessoInsert();
        } else {
            popupMessage_ErroDados();
        }
        this.nivelacessoAux = "";
        atualizaUsuarios();
    }

    public void addMessage(String summary, String detail) {
        FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, summary, detail);
        FacesContext.getCurrentInstance().addMessage(null, message);
    }

    public void popupMessage_SucessoDeleta() {
        addMessage("Sucesso", "Usuário excluído com sucesso.");
    }

    public void popupMessage_ErroDados() {
        addMessage("Erro", "Dados faltantes ou incorretos.");
    }

    public void popupMessage_ErroSenha() {
        addMessage("Erro", "A senha deve ter pelo menos 6 dígitos.");
    }

    public void popupMessage_SucessoInsert() {
        addMessage("Sucesso", "Usuário cadastrado com sucesso.");
    }

    public void popupMessage_SucessoModifica() {
        addMessage("Sucesso", "Usuário modificado com sucesso.");
    }

    public void popupMessage_SucessoModificaSenha() {
        addMessage("Sucesso", "Senha modificada com sucesso.");
    }

    public List<Usuario> getUsuarios() {
        return usuarios;
    }

    public void setUsuarios(List<Usuario> usuarios) {
        this.usuarios = usuarios;
    }

//    public Usuario getSelectedUsuario() {
//        return selectedUsuario;
//    }
//
//    public void setSelectedUsuario(Usuario selectedUsuario) {
//        this.selectedUsuario = selectedUsuario;
//    }
    public Usuario getNovoUsuario() {
        return novoUsuario;
    }

    public void setNovoUsuario(Usuario novoUsuario) {
        this.novoUsuario = novoUsuario;
    }

    public Map<String, String> getNivelacesso() {
        return nivelacesso;
    }

    public void setNivelacesso(Map<String, String> nivelacesso) {
        this.nivelacesso = nivelacesso;
    }

    public String getRegex() {
        return regex;
    }

    public void setRegex(String regex) {
        this.regex = regex;
    }

    public Pattern getPattern() {
        return pattern;
    }

    public void setPattern(Pattern pattern) {
        this.pattern = pattern;
    }

    public String getNivelacessoAux() {
        return nivelacessoAux;
    }

    public void setNivelacessoAux(String nivelacessoAux) {
        this.nivelacessoAux = nivelacessoAux;
    }

    public boolean isPermitirExclusao() {
        return permitirExclusao;
    }

    public void setPermitirExclusao(boolean permitirExclusao) {
        this.permitirExclusao = permitirExclusao;
    }

    public int getTempUserId() {
        return tempUserId;
    }

    public void setTempUserId(int tempUserId) {
        this.tempUserId = tempUserId;
    }

    public String getTempUserLogin() {
        return tempUserLogin;
    }

    public void setTempUserLogin(String tempUserLogin) {
        this.tempUserLogin = tempUserLogin;
    }

    public String getTempUserSenha() {
        return tempUserSenha;
    }

    public void setTempUserSenha(String tempUserSenha) {
        this.tempUserSenha = tempUserSenha;
    }

    public String getTempUserEmail() {
        return tempUserEmail;
    }

    public void setTempUserEmail(String tempUserEmail) {
        this.tempUserEmail = tempUserEmail;
    }

    public String getTempUserNivelAcesso() {
        return tempUserNivelAcesso;
    }

    public void setTempUserNivelAcesso(String tempUserNivelAcesso) {
        this.tempUserNivelAcesso = tempUserNivelAcesso;
    }

    public boolean isDesabilitarMudancaNivelAcesso() {
        return desabilitarMudancaNivelAcesso;
    }

    public void setDesabilitarMudancaNivelAcesso(boolean desabilitarMudancaNivelAcesso) {
        this.desabilitarMudancaNivelAcesso = desabilitarMudancaNivelAcesso;
    }

    public boolean isPermiteMudancaNivelAcesso() {
        return desabilitarMudancaNivelAcesso;
    }

    public void setPermiteMudancaNivelAcesso(boolean permiteMudancaNivelAcesso) {
        this.desabilitarMudancaNivelAcesso = permiteMudancaNivelAcesso;
    }

}
