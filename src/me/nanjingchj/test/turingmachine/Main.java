package me.nanjingchj.test.turingmachine;

public class Main {
    public static void main(String[] args) {
        IMachine machine = new TuringMachine();
        machine.addConfigurationEntry(
                new TuringMachine.ConfigurationEntry(
                        new TuringMachine.MConfiguration('b'),
                        ' ',
                        new IOperation[]{new TuringMachine.Operation("P0"), new TuringMachine.Operation("R")},
                        new TuringMachine.MConfiguration('c')
                )
        );
        machine.addConfigurationEntry(
                new TuringMachine.ConfigurationEntry(
                        new TuringMachine.MConfiguration('c'),
                        ' ',
                        new IOperation[]{new TuringMachine.Operation("R")},
                        new TuringMachine.MConfiguration('d')
                )
        );
        ICompiledMachine compiledMachine = machine.compile();
        compiledMachine.execute();
    }
}
