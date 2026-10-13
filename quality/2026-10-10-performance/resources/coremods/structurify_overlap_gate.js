// CoO 4.6/6.5's runtime toggle cannot prevent descriptor validation against Structurify 2.0.42.
// Target versions are checked per Mixin at runtime, not enforced as loading dependencies.
function initializeCoreMod() {
    var Opcodes = Java.type('org.objectweb.asm.Opcodes');
    var InsnList = Java.type('org.objectweb.asm.tree.InsnList');
    var VarInsnNode = Java.type('org.objectweb.asm.tree.VarInsnNode');
    var LdcInsnNode = Java.type('org.objectweb.asm.tree.LdcInsnNode');
    var MethodInsnNode = Java.type('org.objectweb.asm.tree.MethodInsnNode');
    var JumpInsnNode = Java.type('org.objectweb.asm.tree.JumpInsnNode');
    var LabelNode = Java.type('org.objectweb.asm.tree.LabelNode');
    var InsnNode = Java.type('org.objectweb.asm.tree.InsnNode');
    var ASMAPI = Java.type('net.minecraftforge.coremod.api.ASMAPI');
    return {
        'overlap_gate': {
            'target': {
                'type': 'METHOD',
                'class': 'com.misanthropy.collections_of_optimizations.core.CoOMixinPlugin',
                'methodName': 'shouldApplyMixin',
                'methodDesc': '(Ljava/lang/String;Ljava/lang/String;)Z'
            },
            'transformer': function(method) {
                var keep = new LabelNode();
                var prefix = new InsnList();
                prefix.add(new LdcInsnNode('com.misanthropy.collections_of_optimizations.mixin.structurify.MixinStructureOverlapCheck'));
                prefix.add(new VarInsnNode(Opcodes.ALOAD, 2));
                prefix.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, 'java/lang/String', 'equals', '(Ljava/lang/Object;)Z', false));
                prefix.add(new JumpInsnNode(Opcodes.IFEQ, keep));
                // Runtime check uses the Mixin loader, after Forge mod discovery.
                prefix.add(new MethodInsnNode(Opcodes.INVOKESTATIC, 'hollowrealm/performance/HRFixesMixinPlugin', 'shouldDisableStructurifyOverlap', '()Z', false));
                prefix.add(new JumpInsnNode(Opcodes.IFEQ, keep));
                prefix.add(new InsnNode(Opcodes.ICONST_0));
                prefix.add(new InsnNode(Opcodes.IRETURN));
                prefix.add(keep);
                method.instructions.insert(prefix);
                ASMAPI.log('INFO', 'HR Fixes: installed optional version gate for only the CoO Structurify overlap mixin.');
                return method;
            }
        }
    };
}
