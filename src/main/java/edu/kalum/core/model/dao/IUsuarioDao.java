package edu.kalum.core.model.dao;

import edu.kalum.core.model.entities.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface IUsuarioDao extends JpaRepository<Usuario, Long> {
    public Usuario findByUserName(String username);

    @Query("select u from Usuario u where u.email = ?1")
    public Usuario findByEmail(String email);

}
