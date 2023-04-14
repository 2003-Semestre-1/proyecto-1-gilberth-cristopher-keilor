/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Backend;

import Models.Instruction;
import Models.Memory;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
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
    private String cpuName;
    private List<CPUListener> listeners = new ArrayList<>();
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
        instructionTable.setRowSelectionInterval(0, 0);
    }
    public void executeInstruction(){
        if(currentInstructionPosition < instructionList.size() && memory.getAvailableInstruction()>0){ 
            Instruction instruction = instructionList.get(currentInstructionPosition);
            int remainingTime = instruction.getInstructionRemainingTime();
            if (remainingTime !=1){instruction.setInstructionRemainingTime(remainingTime-1);}
            else {
                switch(instruction.getInstructionOperator()){
                    case "LOAD":
                        fillRegistersUI(memory.executeLoad(instruction), instruction.getInstructionName());
                        break;
                    case "STORE":
                        fillRegistersUI(memory.executeStore(instruction), instruction.getInstructionName());
                        break;
                    case "MOV":
                        fillRegistersUI(memory.executeMov(instruction), instruction.getInstructionName());
                        break;
                    case "SUB":
                        fillRegistersUI(memory.executeSub(instruction), instruction.getInstructionName());
                        break;
                    case "ADD":
                        fillRegistersUI(memory.executeAdd(instruction), instruction.getInstructionName());
                        break;
                    case "INC":
                        fillRegistersUI(memory.executeInc(instruction), instruction.getInstructionName());
                        break;
                    default:
                        notifyInstructionNotImplemented(); //Error Code 00-Instrucctions no yet implemented
                        break;
                }
            }
        }
        else{
            if (ProgramQueue.isEmpty()){
                notifyNoMoreInstructionsAndPrograms(); // Error Code 01-Instrucctions and programs finalised;
            }
            else {
                try {
                    loadNewProgram();
                    notifiedProgramChanged();
                } catch (IOException ex) {
                    notifyLoadingFileError(); // Error Code 02-There been an error loading the next program;
                }
            }
            
        }
        
    }
    
    public void fillRegistersUI(int[] pRegistersValue, String pInstructionBeingExecuted){
        textFieldList.get(0).setText(String.valueOf(pRegistersValue[0]));
        textFieldList.get(1).setText(String.valueOf(pRegistersValue[1]));
        textFieldList.get(2).setText(pInstructionBeingExecuted);
        textFieldList.get(3).setText(String.valueOf(pRegistersValue[5]));
        textFieldList.get(4).setText(String.valueOf(pRegistersValue[4]));
        textFieldList.get(5).setText(String.valueOf(pRegistersValue[3]));
        textFieldList.get(6).setText(String.valueOf(pRegistersValue[2]));
        
        currentInstructionPosition++;
        if(currentInstructionPosition < instructionList.size()){
            instructionTable.setRowSelectionInterval(currentInstructionPosition, currentInstructionPosition);}
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
    
    public String getCPUName(){
        return this.cpuName;
    }
    
    public void setCPUName(String pName){
       this.cpuName = pName;
    }
    
    public int getState(){return this.state;}
    
    public void addListener(CPUListener listener) {
        listeners.add(listener);
    }
    
    public void notifyNoMoreInstructionsAndPrograms() {
        this.state = 0;
        for (CPUListener listener : listeners) {
            listener.onNoMoreInstructionsAndPrograms(this);
        }
    }
    
    public void notifyInstructionNotImplemented() {
        this.state = 0;
        for (CPUListener listener : listeners) {
            listener.onInstructionNotImplemented(this);
        }
    }
    
    public void notifyLoadingFileError() {
        this.state = 0;
        for (CPUListener listener : listeners) {
            listener.onLoadingFileError(this);
        }
    }
    
    public void notifiedProgramChanged() {
        for (CPUListener listener : listeners) {
            listener.onProgramChanged(this);
        }
    }
}
