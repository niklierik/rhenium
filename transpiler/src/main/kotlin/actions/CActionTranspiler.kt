package me.eriknikli.rhenium.transpiler.actions

import dagger.Lazy
import me.eriknikli.rhenium.lowering.actions.*
import me.eriknikli.rhenium.transpiler.IKindTranspiler
import java.io.OutputStream
import javax.inject.Inject
import javax.inject.Singleton

interface IActionTranspiler : IKindTranspiler<Action>

@Singleton
class CActionTranspiler
@Inject
constructor() : IActionTranspiler {
    @Inject
    lateinit var blockTranspilerProvider: Lazy<IBlockTranspiler>

    @Inject
    lateinit var functionTranspilerProvider: Lazy<IFunctionTranspiler>

    @Inject
    lateinit var returnTranspilerProvider: Lazy<IReturnTranspiler>

    @Inject
    lateinit var printTranspilerProvider: Lazy<IPrintTranspiler>

    @Inject
    lateinit var castTranspilerProvider: Lazy<ICastTranspiler>

    @Inject
    lateinit var ternaryTranspilerProvider: Lazy<ITernaryTranspiler>

    @Inject
    lateinit var binaryTranspilerProvider: Lazy<IBinaryTranspiler>

    @Inject
    lateinit var unaryTranspilerProvider: Lazy<IUnaryTranspiler>

    @Inject
    lateinit var varDeclarationTranspilerProvider: Lazy<IVarDeclarationTranspiler>

    @Inject
    lateinit var assignmentTranspilerProvider: Lazy<IAssignmentTranspiler>

    @Inject
    lateinit var expressionStatementTranspilerProvider: Lazy<IExpressionStatementTranspiler>

    @Inject
    lateinit var varRefTranspilerProvider: Lazy<IVarRefTranspiler>

    @Inject
    lateinit var constantTranspilerProvider: Lazy<IConstantTranspiler>

    private val blockTranspiler by lazy { blockTranspilerProvider.get() }
    private val functionTranspiler by lazy { functionTranspilerProvider.get() }
    private val returnTranspiler by lazy { returnTranspilerProvider.get() }
    private val printTranspiler by lazy { printTranspilerProvider.get() }
    private val castTranspiler by lazy { castTranspilerProvider.get() }
    private val ternaryTranspiler by lazy { ternaryTranspilerProvider.get() }
    private val binaryTranspiler by lazy { binaryTranspilerProvider.get() }
    private val unaryTranspiler by lazy { unaryTranspilerProvider.get() }
    private val varDeclarationTranspiler by lazy { varDeclarationTranspilerProvider.get() }
    private val assignmentTranspiler by lazy { assignmentTranspilerProvider.get() }
    private val expressionStatementTranspiler by lazy { expressionStatementTranspilerProvider.get() }
    private val varRefTranspiler by lazy { varRefTranspilerProvider.get() }
    private val constantTranspiler by lazy { constantTranspilerProvider.get() }

    override fun transpile(action: Action, output: OutputStream) {
        when (action) {
            is Block -> blockTranspiler.transpile(action, output)
            is FunctionAction -> functionTranspiler.transpile(action, output)
            is ReturnAction -> returnTranspiler.transpile(action, output)
            is PrintAction -> printTranspiler.transpile(action, output)
            is CastAction -> castTranspiler.transpile(action, output)
            is TernaryAction -> ternaryTranspiler.transpile(action, output)
            is BinaryAction -> binaryTranspiler.transpile(action, output)
            is UnaryAction -> unaryTranspiler.transpile(action, output)
            is VarDeclarationAction -> varDeclarationTranspiler.transpile(action, output)
            is AssignmentAction -> assignmentTranspiler.transpile(action, output)
            is ExpressionStatementAction -> expressionStatementTranspiler.transpile(action, output)
            is VarRefAction -> varRefTranspiler.transpile(action, output)
            is ConstantAction -> constantTranspiler.transpile(action, output)
        }
    }
}
