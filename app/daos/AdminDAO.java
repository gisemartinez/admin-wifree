package daos;

import io.ebean.Ebean;
import io.ebean.Expr;
import models.Admin;

/**
 * Created by jesu on 12/06/17.
 */
public class AdminDAO extends GenericDAO<Admin> {

    public AdminDAO() {
        super(Admin.class);
    }

    public String getPasswordForUser(String email) throws AdminNotFoundException {
        Admin admin = Ebean.find(ENTITY_TYPE)
                .select("password")
                .where()
                .eq("email", email)
                .findOne();
        if (admin == null) {
            throw new AdminNotFoundException();
        }
        return admin.getPassword();
    }

    public Long getPortalForUser(String email) {
        return Ebean.find(ENTITY_TYPE)
                .select("portal")
                .where()
                .eq("email", email)
                .findOne()
                .getPortal()
                .getId();
    }

    public Admin getByEmail(String email) {
        return find(Expr.eq("email", email));
    }

}
