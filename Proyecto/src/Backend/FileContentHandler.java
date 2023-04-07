/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Backend;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import javax.swing.DefaultListModel;
import javax.swing.JList;

import Models.Instruction;
/**
 *
 * @author jimon,Cristopher
 */
public class FileContentHandler {
    
    public FileContentHandler(){
    }
    
    @SuppressWarnings("unchecked") //In this case that warinig is shown because I am parsing directly the result of new FileReader(pSelectedFileRoute)
    public ArrayList<Instruction> getFileContent(String pSelectedFileRoute, JList pContentGuiList) throws FileNotFoundException, IOException{
         DefaultListModel<String> listModel = new DefaultListModel<>();
         ArrayList<Instruction> instructionList= new ArrayList<Instruction>();
        try {
            BufferedReader objReader = new BufferedReader(new FileReader(pSelectedFileRoute));
            String currentLine;
           
            while((currentLine = objReader.readLine() ) !=null){
                if(!currentLine.equals("")){
                    String [] instructionArray = currentLine.split(" ");
                    String operationName = instructionArray[0];
                    String operationRegister = instructionArray[1].substring(0, 2);
                    int operationValue = (operationName.equals("MOV")) ?  Integer.parseInt(instructionArray[2]) : 0;
                    Instruction test = new Instruction(currentLine,operationName,operationRegister,operationValue);
                    instructionList.add(test);   
                    listModel.addElement(currentLine);
                }
            }
            objReader.close();
            pContentGuiList.setModel(listModel);
            return instructionList;
            
        } catch (IOException e) {
            System.out.println("Error: "+ e.getMessage()); //Aqui va a un mensaje de error
        }
        return instructionList;
    }
}
