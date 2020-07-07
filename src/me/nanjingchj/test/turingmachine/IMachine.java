package me.nanjingchj.test.turingmachine;

public interface IMachine {
    void addConfigurationEntry(IConfigurationEntry entry);
    ICompiledMachine compile();
}
