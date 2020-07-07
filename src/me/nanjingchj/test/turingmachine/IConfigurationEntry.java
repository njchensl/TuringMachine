package me.nanjingchj.test.turingmachine;

public interface IConfigurationEntry {
    IMConfiguration getMConfiguration();

    char getSymbol();

    IOperation[] getOperations();

    IMConfiguration getFinalMConfiguration();
}
