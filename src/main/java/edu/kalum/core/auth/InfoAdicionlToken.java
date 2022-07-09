package edu.kalum.core.auth;

import edu.kalum.core.model.dao.services.IUsuarioService;
import edu.kalum.core.model.entities.Usuario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class InfoAdicionlToken implements TokenEnhancer {

    @Autowired
    private IUsuarioService usuarioService;

    @Override
    public OAuth2AccessToken enhance(OAuth2AccessToken oAuth2AccessToken, OAuth2Authentication oAuth2Authentication) {
        Usuario usuario = usuarioService.findByUsername(oAuth2Authentication.getName());
        Map<String, Object> info = new HashMap<>();
        info.put("nombre",usuario.getNombres());
        info.put("apellidos",usuario.getApellidos());
        info.put("email",usuario.getEmail());

        ((DefaultOAuth2AccessToken)oAuth2AccessToken).setAdditionalInformation(info);
        return oAuth2AccessToken;
    }


}
