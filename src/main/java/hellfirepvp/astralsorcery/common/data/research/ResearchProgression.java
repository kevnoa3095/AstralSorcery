package hellfirepvp.astralsorcery.common.data.research;

import hellfirepvp.astralsorcery.AstralSorcery;
import net.minecraft.entity.player.EntityPlayer;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ResearchProgression
 * Created by HellFirePvP
 * Date: 10.08.2016 / 13:38
 */
public enum ResearchProgression {

    DISCOVERY(0, ProgressionTier.DISCOVERY),
    STARLIGHT(1, ProgressionTier.BASIC_CRAFT);

    //TEST_PROGRESS(0, ProgressionTier.DISCOVERY);

    private final int progressId;
    private List<ResearchProgression> preConditions = new LinkedList<>();
    private List<ResearchNode> researchNodes = new LinkedList<>();
    private final ProgressionTier requiredProgress;
    private final String unlocName;

    private static final Map<Integer, ResearchProgression> BY_ID = new HashMap<>();

    private ResearchProgression(int id, ProgressionTier requiredProgress, ResearchProgression... preConditions) {
        this(id, requiredProgress, Arrays.asList(preConditions));
    }

    private ResearchProgression(int id, ProgressionTier requiredProgress, List<ResearchProgression> preConditions) {
        this.preConditions.addAll(preConditions);
        this.requiredProgress = requiredProgress;
        this.progressId = id;
        this.unlocName = AstralSorcery.MODID + ".journal.cluster." + name().toLowerCase() + ".name";
    }

    void addResearchToGroup(ResearchNode res) {
        this.researchNodes.add(res);
    }

    public List<ResearchNode> getResearchNodes() {
        return researchNodes;
    }

    public Registry getRegistry() {
        return new Registry(this);
    }

    public boolean tryStepTo(EntityPlayer player) {
        return canStepTo(player) && ResearchManager.forceUnsafeResearchStep(player, this);
    }

    public boolean canStepTo(EntityPlayer player) {
        PlayerProgress progress = ResearchManager.getProgress(player);
        if(progress == null) return false;
        List<ResearchProgression> playerResearchProgression = progress.getResearchProgression();
        ProgressionTier playerTier = progress.getTierReached();
        return playerTier.isThisLaterOrEqual(requiredProgress) && playerResearchProgression.containsAll(preConditions);
    }

    public ProgressionTier getRequiredProgress() {
        return requiredProgress;
    }

    public List<ResearchProgression> getPreConditions() {
        return Collections.unmodifiableList(preConditions);
    }

    public String getUnlocalizedName() {
        return unlocName;
    }

    public int getProgressId() {
        return progressId;
    }

    public static ResearchProgression getById(int id) {
        return BY_ID.get(id);
    }

    static {
        for (ResearchProgression progress : values()) {
            BY_ID.put(progress.progressId, progress);
        }
    }

    public static class Registry {

        private final ResearchProgression prog;

        public Registry(ResearchProgression prog) {
            this.prog = prog;
        }

        public void register(ResearchNode node) {
            prog.addResearchToGroup(node);
        }

    }

}