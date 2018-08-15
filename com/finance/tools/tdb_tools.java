package com.finance.tools;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.query.Dataset;
import org.apache.jena.query.ReadWrite;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.tdb.TDBFactory;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class tdb_tools {
    public static Logger log=Logger.getLogger(tdb_tools.class);
    public Dataset dataset=null;

    /**
     * 建立tdb数据文件夹
     */
    public tdb_tools(String tdbName){
        dataset=TDBFactory.createDataset(tdbName);
    }

    /**
     * rdf->model
     */
    public void loadModel(String tdb_modelName,OntModel ontmodel,Boolean isOverride){
        int result;
        Model model = dataset.getNamedModel(tdb_modelName);
        //dataset.begin(ReadWrite.WRITE);
        try{
            //已有同名model，且不需要使用新的三元组覆盖旧TDB文件；
            if(dataset.containsNamedModel(tdb_modelName)&&(!isOverride)){
                result=1;
            }
            //没用同名model，或者有同名文件需要覆盖；
            else{
                if (dataset.containsNamedModel(tdb_modelName)) {
                    result = 2;
                    dataset.removeNamedModel(tdb_modelName);//移除已有的model；
                }
                else
                    result = 3;
                model = dataset.getNamedModel(tdb_modelName);//建立一个新的TDB Model，一个TDB可以有多个model，类似数据库的多个表；
                model.begin();//事务开始；
                dataset.addNamedModel(tdb_modelName,ontmodel);//读取ontmodel到；
                model.commit();//将事务提交；
                dataset.commit();

            }
        }catch(Exception e){
            log.error(e.toString());
            result=0;
        }finally{
            if(model !=null && !model.isEmpty())
                System.out.println("success");
            else
                System.out.println("model is empty!");
            model.close();
            dataset.end();
        }
        switch (result){
            case 0:
                log.error(tdb_modelName+":读取model错误！");
                break;
            case 1:
                log.info(tdb_modelName+":已有该model，不需要覆盖！");
                break;
            case 2:
                log.info(tdb_modelName+":已有该model，覆盖原TDB文件，并建立新的model！");
                break;
            case 3:
                log.info(tdb_modelName+":建立新的TDB model！");
                break;
        }
    }
    public void removeModel(String tdb_modelName){
        if(!dataset.isInTransaction())
            dataset.begin(ReadWrite.WRITE);
        try{
            dataset.removeNamedModel(tdb_modelName);
            dataset.commit();
            log.info(tdb_modelName+":已被移除！");
        }finally{
            dataset.end();
        }
    }
    public void removeallModel(){
        List<String> modelname=listModels();
        try{
            for(String name:modelname){
                removeModel(name);
            }
        }finally {
            System.out.println("model已全部删除");
        }

    }
    public void closeTDB(){
        dataset.close();
    }
    public boolean findTDB(String tdb_modelName){
        boolean result;
        dataset.begin(ReadWrite.READ);
        try{
            if(dataset.containsNamedModel(tdb_modelName))
                result=true;
            else
                result=false;
        }finally {
            dataset.end();
        }
        return result;
    }

    /**
     * 列出Dataset中所有的model;
     */
    public List<String> listModels(){
        dataset.begin(ReadWrite.READ);
        List<String> uriList=new ArrayList<>();
        try{
            Iterator<String> names=dataset.listNames();
            String name;
            while(names.hasNext()){
                name=names.next();
                uriList.add(name);
            }
        }finally {
            dataset.end();
        }
        return uriList;
    }
    /**
     * 获得Dataset中某个model；
     */
    public Model getModel(String tdb_modelName) {
        Model model;
        dataset.begin(ReadWrite.READ);
        model = dataset.getNamedModel(tdb_modelName);
        return model;
    }
}
