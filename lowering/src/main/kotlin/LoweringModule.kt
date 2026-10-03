package me.eriknikli.rhenium.lowering

import dagger.Binds
import dagger.Module
import me.eriknikli.rhenium.lowering.tree.expressions.*
import me.eriknikli.rhenium.lowering.tree.statements.*

@Module
interface LoweringModule {
    @Binds
    fun bindLowerer(lowerer: Lowerer): ILowerer

    @Binds
    fun bindStatement(statementLowerer: StatementLowerer): IStatementLowerer

    @Binds
    fun bindPrint(printStatementLowerer: PrintStatementLowerer): IPrintStatementLowerer

    @Binds
    fun bindBlockStatement(blockStatementLowerer: BlockStatementLowerer): IBlockStatementLowerer

    @Binds
    fun bindWhileStatement(whileStatementLowerer: WhileStatementLowerer): IWhileStatementLowerer

    @Binds
    fun bindIfStatement(ifStatementLowerer: IfStatementLowerer): IIfStatementLowerer

    @Binds
    fun bindVarDeclaration(varDeclarationLowerer: VarDeclarationLowerer): IVarDeclarationLowerer

    @Binds
    fun bindVarAssignment(varAssignmentLowerer: VarAssignmentLowerer): IVarAssignmentLowerer

    @Binds
    fun bindCompoundAssignment(compoundAssignmentLowerer: CompoundAssignmentLowerer): ICompoundAssignmentLowerer

    @Binds
    fun bindIncrement(incrementLowerer: IncrementLowerer): IIncrementLowerer

    @Binds
    fun bindExpressionStatement(expressionStatementLowerer: ExpressionStatementLowerer): IExpressionStatementLowerer

    @Binds
    fun bindExpression(expressionLowerer: ExpressionLowerer): IExpressionLowerer

    @Binds
    fun bindLiteral(literalLowerer: LiteralLowerer): ILiteralLowerer

    @Binds
    fun bindLeftValue(leftValueLowerer: LeftValueLowerer): ILeftValueLowerer

    @Binds
    fun bindBinaryOp(binaryOpLowerer: BinaryOpLowerer): IBinaryOpLowerer

    @Binds
    fun bindBinaryActionBuilder(binaryActionBuilder: BinaryActionBuilder): IBinaryActionBuilder

    @Binds
    fun bindUnaryOp(unaryOpLowerer: UnaryOpLowerer): IUnaryOpLowerer
}
