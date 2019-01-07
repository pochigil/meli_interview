package com.greendao;

import de.greenrobot.daogenerator.DaoGenerator;
import de.greenrobot.daogenerator.Entity;
import de.greenrobot.daogenerator.Schema;

public class MyDaoGenerator {

    public static void main(String args[]) throws Exception {

        Schema schema = new Schema(3, "model");

        Entity request = schema.addEntity("Searches");
        request.addIdProperty();
        request.addStringProperty("query");

        new DaoGenerator().generateAll(schema, args[0]);

    }
}