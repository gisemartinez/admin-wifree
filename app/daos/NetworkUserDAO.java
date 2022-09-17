package daos;

import io.ebean.Expr;
import models.NetworkUser;

import javax.annotation.Nullable;

/**
 * Created by jesu on 20/06/17.
 */
public class NetworkUserDAO extends GenericDAO<NetworkUser> {

    public NetworkUserDAO() {
        super(NetworkUser.class);
    }

    @Nullable
    public NetworkUser findByEmail(String email) {
        return find(Expr.eq("email", email));
    }

}
