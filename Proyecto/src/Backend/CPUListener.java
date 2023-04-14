/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package Backend;

/**
 *
 * @author Daniel
 */
public interface CPUListener {
    public void onNoMoreInstructionsAndPrograms(CPUController cpu);
    public void onInstructionNotImplemented(CPUController cpu);
    public void onLoadingFileError(CPUController cpu);
    public void onProgramChanged(CPUController cpu);
}
