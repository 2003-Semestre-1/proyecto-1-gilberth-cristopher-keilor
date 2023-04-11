/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Backend;

import Models.Instruction;
import Models.Memory;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
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
    private JTable instructionTable;
    private ArrayList<String> ProgramQueue;
    private int state = 0; 
    public CPUController(ArrayList<JTextField> pTextFieldList){
        setTextFieldList(pTextFieldList);
    }
    
    
    
    public boolean loadInstructions(ArrayList<String> files,JTable pContentTable) throws IOException{
        ProgramQueue = files;
        instructionTable = pContentTable;
        if (ProgramQueue.isEmpty()){ this.state = 0; return true;}
        else {
            return loadInstructions_aux();
        }      
    }
    
    public boolean loadInstructions_aux() {
        try {
            instructionList = FileContent.getFileContent(ProgramQueue.get(0));
        } catch (IOException ex) {
            Logger.getLogger(CPUController.class.getName()).log(Level.SEVERE, null, ex);
        }
        ProgramQueue.remove(0);
        int necesaryMemory = instructionList.size();
        if (this.memory.getMemorySize()>= necesaryMemory){
            memory.updateInicialPC(necesaryMemory);
            showInstrucctions();
            this.state = 1;
            return true;
        }else{return false;}
    }      
    
    
    public void showInstrucctions(){
        DefaultTableModel tblModel = (DefaultTableModel) instructionTable.getModel();
        tblModel.setRowCount(0);
        int memory_position = memory.getMemoryPosition();
        int contador = 0;
        for (Instruction instruction: instructionList) {
            String instrucctionCompleta = instruction.getInstructionOperator()+" "+
                    instruction.getInstructionRegister()+ ", " +instruction.getInstructionNumberValue();
            String data[] = {String.valueOf(contador), String.valueOf(memory_position),instrucctionCompleta,
            String.valueOf(instruction.getInstructionWeight()),"READY"};
            tblModel.addRow(data);
            contador++;
            memory_position++;
        }
    }
    public String executeInstruction(){
        if(currentInstructionPosition < instructionList.size() && memory.getAvailableInstruction()>0){ 
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
            if (ProgramQueue.isEmpty()){
                return "Error01"; // Error Code 01-Instrucctions and programs finalised;
            }
            else {
                try {
                    loadNewProgram();
                    return "ProgramChanged";
                } catch (IOException ex) {
                    return "Error02"; // Error Code 02-There been an error loading the next program;
                }
            }
            
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
        currentInstructionPosition++;
        return "Success";
    }
    
    public void loadNewProgram() throws IOException{
        resetValues();
        loadInstructions_aux();
    }
    
    public void resetValues(){
        memory.resetMemoryRegister();
        currentInstructionPosition=0;  
    }
    
    
    public void setMemorySize(int pMemorySize){
        memory = new Memory(pMemorySize);
    }
    
    public void setTextFieldList(ArrayList<JTextField> pTextFieldList){
        this.textFieldList = pTextFieldList;
    }
    
    public int getRemainingPrograms(){
        return this.ProgramQueue.size();
    }
    
    public int getState(){return this.state;}
}
