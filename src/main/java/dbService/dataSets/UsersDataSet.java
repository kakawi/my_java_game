package dbService.dataSets;

import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "users")
public class UsersDataSet implements Serializable { // Serializable Important to Hibernate!
    private static final long serialVersionUID = -8706689714326132798L;

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "login", unique = true, updatable = false)
    private String login;

    @Column(name = "password", updatable = false)
    private String password;

    @Column(name = "email", unique = true, updatable = false)
    private String email;

    @Column(name = "wins")
    @Type(type = "int")
    private Integer wins = 0;

    @Column(name = "defeats")
    @Type(type = "int")
    private Integer defeats = 0;

    @Column(name = "draws")
    @Type(type = "int")
    private Integer draws = 0;

    public Integer getWins() {
        return wins;
    }

    public void setWins(Integer wins) {
        this.wins = wins;
    }

    public Integer getDefeats() {
        return defeats;
    }

    public void setDefeats(Integer defeats) {
        this.defeats = defeats;
    }

    public Integer getDraws() {
        return draws;
    }

    public void setDraws(Integer draws) {
        this.draws = draws;
    }

    public String getPassword() {

        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    //Important to Hibernate!
    @SuppressWarnings("UnusedDeclaration")
    public UsersDataSet() {
    }

    @SuppressWarnings("UnusedDeclaration")
    public UsersDataSet(long id, String name) {
        this.setId(id);
        this.setLogin(name);
    }

    public UsersDataSet(String name) {
        this.setId(-1);
        this.setLogin(name);
    }

    public UsersDataSet(String login, String password, String email) {
        this.login = login;
        this.password = password;
        this.email = email;
    }

    @SuppressWarnings("UnusedDeclaration")
    public String getLogin() {
        return login;
    }

    public void setLogin(String name) {
        this.login = name;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "UserDataSet{" +
                "id=" + id +
                ", name='" + login + '\'' +
                '}';
    }

    public void increaseWins() {
        wins++;
    }

    public void increaseDefeats() {
        defeats++;
    }

    public void increaseDraws() {
        draws++;
    }
}
