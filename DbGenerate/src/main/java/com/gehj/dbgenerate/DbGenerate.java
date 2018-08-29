package com.gehj.dbgenerate;


import org.greenrobot.greendao.generator.DaoGenerator;
import org.greenrobot.greendao.generator.Entity;
import org.greenrobot.greendao.generator.Schema;

public class DbGenerate {
    public static void main(String[] args) {
        //生成数据：
        Schema  schema =  new Schema(1,"com.netframe.db");//数据库与版本；
        Entity entity = schema.addEntity("DownLoadEntity");
        entity.addLongProperty("start_pos");//添加数据库字段；
        entity.addLongProperty("end_pos");//添加数据库字段；
        entity.addLongProperty("progress_pos");//添加数据库字段；
        entity.addStringProperty("download_url");//添加数据库字段；
        entity.addIntProperty("thread_id");//添加数据库字段；
        entity.addIdProperty().autoincrement();//主键自增；

        try {
            new DaoGenerator().generateAll(schema,"DbGenerate/gen");
        } catch (Exception e) {
            e.printStackTrace();
        }


    }
}
