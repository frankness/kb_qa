package com.finance;

import com.finance.finace_message_class.basic_info;
import com.finance.finace_message_class.gggd;
import com.finance.finace_message_class.sequential_financial;
import org.apache.jena.base.Sys;
import org.apache.jena.ontology.Individual;
import org.apache.jena.ontology.OntClass;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntModelSpec;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.shared.InvalidPropertyURIException;
import org.apache.log4j.Logger;
import java.io.*;
import org.apache.log4j.FileAppender;
import org.apache.log4j.SimpleLayout;
import org.apache.log4j.HTMLLayout;
import org.apache.log4j.PatternLayout;
import java.util.ArrayList;
import java.util.List;

public class addinstance_to_concept {
    public static String source="http://www.semanticweb.org/renyi/ontologies/2018/5/untitled-ontology-3#";
    private static Logger logger = Logger.getLogger(addinstance_to_concept.class);

    public static void logger_set(){
        PatternLayout layout = new PatternLayout();
        FileAppender filelog = null;
        try{
            filelog = new FileAppender(layout,"src/data/kg.log",false);
        }catch (Exception e)
        {
            //System.out.println(e);
        }
        logger.addAppender(filelog);
    }
    public static OntModel getmodel(String path){
        OntModel ontModel= ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM);
        File file=new File(path);
        FileInputStream in=null;
        try {
            in=new FileInputStream(file);
        } catch (FileNotFoundException e) {
            //System.out.println(e);
        }
        ontModel.read(in,null);
        return ontModel;
    }
    public static void writetordf(OntModel mymodel,String path){
        FileOutputStream out_file=null;
        try {
//            out_file = new FileOutputStream("src/output/outtest.owl");
            out_file = new FileOutputStream(path);
        } catch (FileNotFoundException e) {
            //System.out.println(e);
        };
        try{
            mymodel.write(out_file, "RDF/XML");
        }catch (Exception e){

        }

        try {
            out_file.close();
        } catch (IOException e) {
           // System.out.println(e);
        }
    }
    public static Individual hasindividual_or_create(OntClass myclass,OntModel mymodel,String to_find){
        Individual findindividual=null;
        try{
            findindividual=mymodel.getIndividual(source+to_find);
        }catch (Exception e){
            System.out.println("wrong0");
        }
        if(findindividual!=null){
            //System.out.println(String.format("found %s",to_find));
            logger.info(String.format("found %s",to_find));
            if(findindividual.getOntClass().equals(myclass))
                return findindividual;
            else{
                logger.info(String.format("%s not in class:%s",to_find,myclass.getLocalName()));
                try{
                    Individual newind=myclass.createIndividual(source+to_find);
                    // System.out.println(String.format("%s not in this class",to_find));
                    return newind;
                }catch (Exception e){
                    System.out.println("wrong1");
                    logger.info(String.format("Can not create %s of class:%s",to_find,myclass.getLocalName()));
                }

            }

        }
        else{
           // System.out.println(String.format("create new individual:%s",to_find));
            try{
                Individual newind=myclass.createIndividual(source+to_find);
                return newind;
            }catch (Exception e){
                System.out.println("wrong2");
                logger.info(String.format("Can not create %s of class:%s",to_find,myclass.getLocalName()));
            }

        }
        return null;
    }
    public static Individual add_relationship(OntModel ontmodel,Individual thisitem,String relationship,String individual_value,OntClass thisclass){
        if(!(individual_value==null)&&!(thisitem==null)&&!(relationship==null))
        {
            if (!(individual_value.equals("-")) &&  (!individual_value.equals("--")) && (!individual_value.isEmpty())){
                Individual thisindividual=null;
                try{
                    Property thisproprty=ontmodel.getObjectProperty(source+relationship);
                    thisindividual=hasindividual_or_create(thisclass,ontmodel,individual_value);
                    thisitem.addProperty(thisproprty,thisindividual);
                    return thisindividual;
                }
                catch (NullPointerException e ){
                    logger.info(String.format("%s fail to add relationship %s to %s",thisitem.getLocalName(),relationship,individual_value)+"cause:"+e.getMessage());
                    //System.out.println(String.format("No such property!:%s",relationship));

                }
            }
        }

        return null;
    }
    public static void add_dataproperty(OntModel ontmodel,Individual thisitem,String dataproperty,String value){
        if(!(thisitem==null) && !(dataproperty==null) && !(value==null)){
            if(!(dataproperty.isEmpty())&&!(value.equals("-")) && !(value.equals("--")) && !(value.isEmpty()&&value!=null)){
                try{
                    Property thisproprty=ontmodel.getDatatypeProperty(source+dataproperty);
                    thisitem.addProperty(thisproprty,value);
                }
                catch (Exception e)
                {
                    logger.info(String.format("Fail to add %s to %s",dataproperty,thisitem.getLocalName())+",cause:"+e.getCause());
                    //System.out.println(thisitem+":"+dataproperty);
                    //System.out.println(e);
                }
            }
        }


    }
    public static OntModel add_basicinfo(OntModel ontModel,String file_path,int fileindex){
        basic_info basic_message=get_excelmessage.get_basic_information(file_path,fileindex);
        //System.out.println(basic_message.basic_information);

        OntClass company=ontModel.getOntClass(source+"公司");
        OntClass person=ontModel.getOntClass(source+"人");
        OntClass address=ontModel.getOntClass(source+"地点");
        logger.info("\t---添加基本信息---");

        Individual thiscompany=hasindividual_or_create(company,ontModel,basic_message.companyname);
        try{
            add_dataproperty(ontModel,thiscompany,"证券代码",basic_message.basic_information.get("证券代码"));
            add_dataproperty(ontModel,thiscompany,"证券代码",basic_message.basic_information.get("证券代码"));
            add_dataproperty(ontModel,thiscompany,"公司全称",basic_message.basic_information.get("公司中文全称"));
            add_dataproperty(ontModel,thiscompany,"证券简称",basic_message.basic_information.get("证券简称"));
            add_relationship(ontModel,thiscompany,"法人",basic_message.basic_information.get("法定代表人"),person);
            Individual broker_sponser=add_relationship(ontModel,thiscompany,"主办券商",basic_message.basic_information.get("主办券商"),company);
            add_relationship(ontModel,thiscompany,"办公地址",basic_message.basic_information.get("办公地址"),address);
            add_relationship(ontModel,thiscompany,"注册地址",basic_message.basic_information.get("注册地址"),address);
            add_relationship(ontModel,broker_sponser,"办公地址",basic_message.basic_information.get("主办券商办公地址"),address);
            Individual account_firm=add_relationship(ontModel,thiscompany,"会计师事务所",basic_message.basic_information.get("会计师事务所"),company);
            add_relationship(ontModel,account_firm,"办公地址",basic_message.basic_information.get("会计师事务所办公地址"),address);
            String[] accountants= basic_message.basic_information.get("签字注册会计师姓名").split("、|\\s+");
            for (String str:accountants){
                add_relationship(ontModel,thiscompany,"签字注册会计师",str,person);
            }
        }catch (Exception e){

        }
        return ontModel;
    }
    public static OntModel add_gaoguan(OntModel ontModel,String file_path,int fileindex){
        gggd gggd_message=get_excelmessage.get_gggd(file_path,fileindex);
        List<List> gaoguanlist=gggd_message.gaoguan;
        OntClass company=ontModel.getOntClass(source+"公司");
        OntClass person=ontModel.getOntClass(source+"人");
        logger.info("\t---添加高管---");
        Individual thiscompany=hasindividual_or_create(company,ontModel,gggd_message.companyname);
        try{
            for(int i=1;i<gaoguanlist.size();i++){
                List<String> gaoguan_message=gaoguanlist.get(i);
                String[] positions=gaoguan_message.get(1).split("、|兼|和");
                Individual person_individual=null;
                for(String str:positions){
                    person_individual=add_relationship(ontModel,thiscompany,str,gaoguan_message.get(0),person);
                    //System.out.println(str);//职位
                }
                add_dataproperty(ontModel,person_individual,"姓名",gaoguan_message.get(0));
                add_dataproperty(ontModel,person_individual,"性别",gaoguan_message.get(2));
                add_dataproperty(ontModel,person_individual,"学历",gaoguan_message.get(4));
            }
        }catch (Exception e){
            logger.warn("添加高管时，"+e.getMessage());
            //System.out.println(e);
        }

        return ontModel;
    }
    public static OntModel add_gudong(OntModel ontModel,String file_path,int fileindex,int year){
        gggd gggd_message=get_excelmessage.get_gggd(file_path,fileindex);
        List<List> gudonglist=gggd_message.gudong;
        //System.out.println(gudonglist);
        logger.info("\t---添加股东---" );
        OntClass company=ontModel.getOntClass(source+"公司");
        OntClass person=ontModel.getOntClass(source+"人");
        OntClass stock_amount=ontModel.getOntClass(source+"股票数量");
        Individual thiscompany=hasindividual_or_create(company,ontModel,gggd_message.companyname);
        int listsize=gudonglist.get(gudonglist.size()-1).size()==gudonglist.get(gudonglist.size()-2).size()?gudonglist.size():gudonglist.size()-1;

        for(int i=1;i<listsize;i++){
            try{
                List<String> gudong=gudonglist.get(i);
                Individual this_person=hasindividual_or_create(person,ontModel,gudong.get(1));
                Individual this_amount=hasindividual_or_create(stock_amount,ontModel,String.format("%s%s持股%s数量",gudong.get(1),Integer.toString(year)+"年" ,gggd_message.companyname));
                if(gudong.get(1).length()<=3) {
                    add_relationship(ontModel,thiscompany,"股东",gudong.get(1),person);
                }
                else{
                    add_relationship(ontModel,thiscompany,"股东",gudong.get(1),company);
                }
                add_dataproperty(ontModel,this_amount,"股票总数",gudong.get(4));
                add_dataproperty(ontModel,this_amount,"限售股份数量",gudong.get(6));
                add_dataproperty(ontModel,this_amount,"无限售股份数量",gudong.get(7));
                add_dataproperty(ontModel,this_amount,"时间",Integer.toString(year));
                add_relationship(ontModel,this_person,"has",String.format("%s%s持股%s数量",gudong.get(1),Integer.toString(year)+"年" ,gggd_message.companyname),stock_amount);
            }catch (Exception e) {
                //System.out.println(e);
                logger.warn("添加股东时，"+e.getMessage());
            }
        }
        try{
            if(listsize<gudonglist.size()){
            List<String> relationships=gudonglist.get(gudonglist.size()-1);
            String [] relationship_list=relationships.get(0).split("&&");
            for (String items:relationship_list){
                String [] ind=items.split(" ");
                Individual first=hasindividual_or_create(person,ontModel,ind[0]);
                add_relationship(ontModel,first,ind[2],ind[1],person);
                }
           }
        }catch (Exception e){
            logger.warn("添加股东关系时，"+e.getMessage());
            //System.out.println(e);
        }

        return ontModel;
    }
    public static void deal_financial(OntModel ontModel,Individual thiscompany,String classname,List<List> financal_message,int year){
        if (!financal_message.isEmpty()) {
            OntClass financal_class=ontModel.getOntClass(source+classname);
            if (classname == "非经常性损益") {
                Individual this_individual = financal_class.createIndividual(source + thiscompany.getLocalName() + String.valueOf(year)+"年" + classname);
                try {
                    for (int i = 1; i < financal_message.size(); i++) {
                        List specify_message = financal_message.get(i);
                        for (int j = 0; j < specify_message.size(); j++) {
                            add_dataproperty(ontModel, this_individual, specify_message.get(0).toString(), specify_message.get(1).toString());
                        }
                    }
                } catch (Exception e) {
                    logger.warn("添加非经常性损益时，"+e.getMessage());
                   //System.out.println(e);
                }
                add_relationship(ontModel, thiscompany, "has", thiscompany.getLocalName() + String.valueOf(year)+"年"  + classname, financal_class);
            } else {
                Individual this_individual = financal_class.createIndividual(source + thiscompany.getLocalName() + String.valueOf(year) +"年" + classname);
                Individual last_individual = financal_class.createIndividual(source + thiscompany.getLocalName() + String.valueOf(year - 1)+"年"  + classname);
                try {
                    for (int i = 1; i < financal_message.size(); i++) {
                        List specify_message = financal_message.get(i);
                        for (int j = 0; j < specify_message.size(); j++) {
                            add_dataproperty(ontModel, this_individual, specify_message.get(0).toString(), specify_message.get(1).toString());
                            add_dataproperty(ontModel, last_individual, specify_message.get(0).toString(), specify_message.get(2).toString());
                        }
                    }
                } catch (Exception e) {
                    logger.warn(String.format("添加%s时,",classname)+e.getMessage());
                    //System.out.println(e);
                }
                add_relationship(ontModel, thiscompany, "has", thiscompany.getLocalName() + String.valueOf(year)+"年"  + classname, financal_class);
                add_relationship(ontModel, thiscompany, "has", thiscompany.getLocalName() + String.valueOf(year - 1)+"年"  + classname, financal_class);
            }
        }
    }
    public static OntModel add_sequential_financial(OntModel ontModel,String file_path,int fileindex,int year){
        sequential_financial financial_message=get_excelmessage.get_financial(file_path,fileindex);
        List<List> profitability=financial_message.profitability;//盈利能力
        List<List> solvency=financial_message.solvency;//偿债能力
        List<List> operation=financial_message.operation;//营运情况
        List<List> growth=financial_message.growth;//成长情况
        List<List> capital_stock=financial_message.capital_stock;//普通股总股
        List<List> non_recurrent_lossprofit=financial_message.non_recurrent_lossprofit;//非经常性损益
        //System.out.println(profitability);
        OntClass company=ontModel.getOntClass(source+"公司");
        Individual thiscompany=hasindividual_or_create(company,ontModel,financial_message.companyname);
        logger.info("\t---添加盈利能力---");
        deal_financial(ontModel,thiscompany,"盈利能力",profitability,year);
        logger.info("\t---添加偿债能力---");
        deal_financial(ontModel,thiscompany,"偿债能力",solvency,year);
        logger.info("\t---添加营运情况---");
        deal_financial(ontModel,thiscompany,"营运情况",operation,year);
        logger.info("\t---添加成长情况---");
        deal_financial(ontModel,thiscompany,"成长情况",growth,year);
        logger.info("\t---添加股本情况---");
        deal_financial(ontModel,thiscompany,"股本情况",capital_stock,year);
        logger.info("\t---添加非经常性损益---");
        deal_financial(ontModel,thiscompany,"非经常性损益",non_recurrent_lossprofit,year);
        return ontModel;
    }
    public static void deal_sup_cus(OntModel ontModel,Individual thiscompany,List<List> message,String relationship,int year){
        OntClass company=ontModel.getOntClass(source+"公司");
        try {
            for(int i=1;i<message.size();i++){
                List specify_mesage=message.get(i);
                //Individual aim_individual=hasindividual_or_create(company,ontModel,specify_mesage.get(1).toString());
                add_relationship(ontModel,thiscompany,relationship,specify_mesage.get(1).toString(),company);
            }
        }catch (Exception e){
            logger.warn(String.format("添加%s时,",relationship)+e.getMessage());
            //System.out.println(e);
        }

    }
    public static OntModel add_sup_cus(OntModel ontModel,String file_path,int fileindex,int year){
        basic_info basic_message=get_excelmessage.get_basic_information(file_path,fileindex);
        OntClass company=ontModel.getOntClass(source+"公司");
        Individual thiscompany=hasindividual_or_create(company,ontModel,basic_message.companyname);
        List<List> supplier=basic_message.supplier;
        List<List> customer=basic_message.customer;
        logger.info("\t---添加供应商---");
        deal_sup_cus(ontModel,thiscompany,supplier,"供应商",year);
        logger.info("\t---添加客户---");
        deal_sup_cus(ontModel,thiscompany,customer,"客户",year);
        return ontModel;
    }
    public static OntModel add_all(OntModel ontModel,String file_path,int fileindex,int year){
        ontModel=add_basicinfo(ontModel,file_path,fileindex);
        ontModel=add_gudong(ontModel,file_path,fileindex,year);
        ontModel=add_gaoguan(ontModel,file_path,fileindex);
        ontModel=add_sequential_financial(ontModel,file_path,fileindex,year);
        ontModel=add_sup_cus(ontModel,file_path,fileindex,year);
        writetordf(ontModel,"src/output/outtest.owl");
        return ontModel;

    }
    public static void main(String[] args){
        logger_set();
        String concept_path="C:\\Users\\renyi\\IdeaProjects\\finance_knowledgegraph\\src\\data\\concept_graphrdf.owl";
        String file_path="C:\\Users\\renyi\\IdeaProjects\\finance_knowledgegraph\\src\\data\\2016wordextra";
        OntModel ontModel=getmodel(concept_path);
        add_all(ontModel,file_path,0,2016);

    }
}
