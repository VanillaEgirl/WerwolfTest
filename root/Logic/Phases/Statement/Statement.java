package root.Logic.Phases.Statement;

import root.Logic.Persona.Fraktion;
import root.Logic.Persona.Rolle;
import root.Logic.Phases.Statement.Constants.StatementState;
import root.Logic.Phases.Statement.Constants.StatementType;
import root.Logic.Phases.Statement.StatementDependency.StatementDependency;
import root.Logic.Phases.Statement.StatementDependency.StatementDependencyFraktion;
import root.Logic.Phases.Statement.StatementDependency.StatementDependencyRolle;

public class Statement {
    public String id;
    public String title;
    public String beschreibung;
    public StatementType type;
    public StatementDependency dependency;

    public StatementState state = StatementState.INVISIBLE_NOT_IN_GAME;
    public boolean alreadyOver = false;

    public Statement() {
        type = StatementType.EMPTY_STATEMENT;
    }

    public Statement(Rolle rolle) {
        this.id = rolle.statementID;
        this.title = rolle.statementTitle;
        this.beschreibung = rolle.statementBeschreibung;
        this.type = rolle.statementType;
        this.dependency = new StatementDependencyRolle(rolle);
    }

    public Statement(Fraktion fraktion) {
        this.id = fraktion.statementID;
        this.title = fraktion.statementTitle;
        this.beschreibung = fraktion.statementBeschreibung;
        this.type = fraktion.statementType;
        this.dependency = new StatementDependencyFraktion(fraktion);
    }

    public void refreshState() {
        state = dependency.getState();
    }
}
