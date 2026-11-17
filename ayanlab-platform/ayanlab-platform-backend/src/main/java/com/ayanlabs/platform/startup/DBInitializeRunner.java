package com.ayanlabs.platform.startup;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DBInitializeRunner implements CommandLineRunner {

    @Value("${platform.db.init-on-startup:false}")
    private boolean initOnStartup;

    @Value("${platform.db.rename-on-startup:false}")
    private boolean renameOnStartup;

    @Override
    public void run(String... args) {
        if (initOnStartup || renameOnStartup) {
            System.out.println("[DBInitializeRunner] SQL scripts are disabled. Hibernate/JPA will manage schema creation (spring.jpa.hibernate.ddl-auto=update).\nIf you previously relied on manual SQL, set platform.db.init-on-startup and ensure scripts exist.");
        }
    }
}
