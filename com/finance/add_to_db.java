package com.finance;
import com.finance.tools.excel_tools;
import com.finance.tools.tdb_tools;
import org.apache.jena.ontology.OntModel;

import java.util.ArrayList;

public class add_to_db {
    public static void main(String[] args){
        addinstance_to_concept newinstance=new addinstance_to_concept();
        String concept_path="C:\\Users\\renyi\\IdeaProjects\\finance_knowledgegraph\\src\\data\\concept_graphrdf.owl";
        String file_path="C:\\Users\\renyi\\IdeaProjects\\finance_knowledgegraph\\src\\data\\2016wordextra";
        ArrayList<String> file_names=excel_tools.getFiles(file_path);
        tdb_tools tdb_ds=new tdb_tools("D:\\finance_tdb");
        Boolean flag=true;
        OntModel ontModel=newinstance.getmodel(concept_path);
        System.out.println(String.format("Total:%d files",file_names.size()));
        for(int i=0;i<file_names.size();i++){
            try{
                ontModel=newinstance.add_all(ontModel,file_path,i,2016);
                System.out.println(String.format("Success:NO.%d file",i+1));
            }catch (Exception e){
                System.out.println(String.format("Fail:NO.%d file",i+1));
                e.printStackTrace();
                System.out.println(e.getMessage());
            }
        }
        try{
            tdb_ds.loadModel("fiance2016",ontModel,flag);
            System.out.println("Succeed to insert to database");
        }catch (Exception e){
            System.out.println("Fail to insert to database");
            System.out.println(e.getMessage());
        }

        tdb_ds.closeTDB();
    }

}
