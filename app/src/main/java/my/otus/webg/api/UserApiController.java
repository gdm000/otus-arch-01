package my.otus.webg.api;

import my.otus.webg.model.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import javax.sql.DataSource;
import javax.validation.Valid;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.SpringCodegen", date = "2021-02-01T18:56:59.602Z[GMT]")
@RestController
public class UserApiController implements UserApi {

    private static final Logger log = LoggerFactory.getLogger(UserApiController.class);

    private final ObjectMapper objectMapper;

    private final NamedParameterJdbcTemplate jdbc;

    @org.springframework.beans.factory.annotation.Autowired
    public UserApiController(ObjectMapper objectMapper, DataSource ds) {
        this.objectMapper = objectMapper;
        this.jdbc = new NamedParameterJdbcTemplate(ds);
    }

    public ResponseEntity createUser(@Parameter(in = ParameterIn.DEFAULT, description = "Created user object", required=true, schema=@Schema()) @Valid @RequestBody User body) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbc.update("insert into users (username, first_name, last_name, email, phone) values (:username, :first_name, :last_name, :email, :phone)", new MapSqlParameterSource()
        .addValue("username", body.getUsername())
        .addValue("first_name", body.getFirstName())
        .addValue("last_name", body.getLastName())
        .addValue("email", body.getEmail())
        .addValue("phone", body.getPhone()),
        keyHolder);
        Object key = keyHolder.getKeys().get("id");
        log.info("Saved with new id: {} : {}", key, body);
        return ResponseEntity.ok(key);
    }

    public ResponseEntity<Void> deleteUser(@Parameter(in = ParameterIn.PATH, description = "ID of user", required=true, schema=@Schema()) @PathVariable("userId") Long userId) {
        boolean res = jdbc.update("delete from users where id = :id", new MapSqlParameterSource("id", userId)) > 0;
        log.info("User {} removed: {}", userId, res);
        return new ResponseEntity<Void>(res ? HttpStatus.OK : HttpStatus.NOT_FOUND) ;
    }

    public ResponseEntity findUserById(@Parameter(in = ParameterIn.PATH, description = "ID of user", required=true, schema=@Schema()) @PathVariable("userId") Long userId) {
        try {
            User user = jdbc.queryForObject("select * from users where id = :id", new MapSqlParameterSource("id", userId), new BeanPropertyRowMapper<>(User.class));
            return ResponseEntity.ok(user);
        } catch (EmptyResultDataAccessException e) {
            return (ResponseEntity) ResponseEntity.notFound().build();
        }
    }

    public ResponseEntity<Void> updateUser(@Parameter(in = ParameterIn.PATH, description = "ID of user", required=true, schema=@Schema()) @PathVariable("userId") Long userId,@Parameter(in = ParameterIn.DEFAULT, description = "", schema=@Schema()) @Valid @RequestBody User body) {
        int updated = jdbc.update("update users set username=:username, first_name=:first_name, last_name=:last_name, email=:email, phone=:email where id=:id", new MapSqlParameterSource()
                        .addValue("id", userId)
                        .addValue("username", body.getUsername())
                        .addValue("first_name", body.getFirstName())
                        .addValue("last_name", body.getLastName())
                        .addValue("email", body.getEmail())
                        .addValue("phone", body.getPhone()));
        log.info("Updated {} with id: {} {}", updated, userId, body);
        return new ResponseEntity<Void>(updated > 0 ? HttpStatus.OK : HttpStatus.NOT_FOUND);
    }

}
