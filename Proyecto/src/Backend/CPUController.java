/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Backend;

import Models.Instruction;
import Models.Memory;
import java.io.IOException;
import java.util.ArrayList;
import javax.swing.JList;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author jimon,Cristopher
 */
public class CPUController {
    
    private FileContentHandler FileContent = new FileContentHandler();
    private int currentInstructionPosition=0;
    
    private ArrayList<Instruction> instructionList;
    private Memory memory;
    private ArrayList<JTextField> textFieldList;
    private JTable contentTable;
    public CPUController(ArrayList<JTextField> pTextFieldList){
        setTextFieldList(pTextFieldList);
    }
    
    
    public boolean loadInstructions(String pSelectedFileRoute, JList pContentGuiList, JTable pContentTable) throws IOException{
        
        instructionList = FileContent.getFileContent(pSelectedFileRoute, pContentGuiList);
        contentTable = pContentTable;
        int necesaryMemory = instructionList.size();
        if (this.memory.getMemorySize()>= necesaryMemory){
            memory.updateInicialPC(necesaryMemory);
            return true;
        }else{return false;}
    }
    
    public String executeInstruction(){
        if(currentInstructionPosition < instructionList.size() && memory.getAvailableInstruction()>0){ 
            //Aqui es donde tengo que cambiar el color del fondo para saber que funcion estoy ejecutando
            Instruction instruction = instructionList.get(currentInstructionPosition);
            
            switch(instruction.getInstructionOperator()){
                case "LOAD":
                    return fillRegistersUI(memory.executeLoad(instruction), instruction.getInstructionName());
                case "STORE":
                    return fillRegistersUI(memory.executeStore(instruction), instruction.getInstructionName());
                case "MOV":
                    return fillRegistersUI(memory.executeMov(instruction), instruction.getInstructionName());
                case "SUB":
                    return fillRegistersUI(memory.executeSub(instruction), instruction.getInstructionName());
                case "ADD":
                    return fillRegistersUI(memory.executeAdd(instruction), instruction.getInstructionName());
                default:
                    return "Error00"; //Error Code 00-Instruccion not yet implemented
            }
        }
        else{
            return "Error01"; //Error Code 01 - List of instruccion finalised
        }
    }
    
    public String fillRegistersUI(int[] pRegistersValue, String pInstructionBeingExecuted){
        textFieldList.get(0).setText(String.valueOf(pRegistersValue[0]));
        textFieldList.get(1).setText(String.valueOf(pRegistersValue[1]));
        textFieldList.get(2).setText(pInstructionBeingExecuted);
        textFieldList.get(3).setText(String.valueOf(pRegistersValue[5]));
        textFieldList.get(4).setText(String.valueOf(pRegistersValue[4]));
        textFieldList.get(5).setText(String.valueOf(pRegistersValue[3]));
        textFieldList.get(6).setText(String.valueOf(pRegistersValue[2]));
        String data[] = {String.valueOf(pRegistersValue[0]), instructionList.get(currentInstructionPosition).getBinaryCode()};
        DefaultTableModel tblModel = (DefaultTableModel) contentTable.getModel();
        tblModel.addRow(data);
        currentInstructionPosition++;
        return "Success";
    }
    
    public void setMemorySize(int pMemorySize){
        memory = new Memory(pMemorySize);
    }
    
    public void setTextFieldList(ArrayList<JTextField> pTextFieldList){
        this.textFieldList = pTextFieldList;
    }
    
    
}
