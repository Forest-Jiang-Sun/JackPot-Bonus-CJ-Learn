package com.aspectgaming.gdx.component.drawable.progressivereel;

import com.aspectgaming.common.actor.*;
import com.aspectgaming.common.configuration.GameConfiguration;
import com.aspectgaming.common.data.Content;
import com.aspectgaming.common.data.GameConst;
import com.aspectgaming.common.data.GameData;
import com.aspectgaming.common.event.EventMachine;
import com.aspectgaming.common.event.freegame.RetriggerAnimEndEvent;
import com.aspectgaming.common.event.game.*;
import com.aspectgaming.common.event.machine.InTiltEvent;
import com.aspectgaming.common.event.machine.LanguageChangedEvent;
import com.aspectgaming.common.event.machine.OutTiltEvent;
import com.aspectgaming.common.loader.*;
import com.aspectgaming.common.video.Video;
import com.aspectgaming.gdx.component.drawable.DrawableComponent;
import com.aspectgaming.gdx.component.drawable.reel.Symbol;
import com.aspectgaming.net.game.GameClient;
import com.aspectgaming.net.game.data.SettingData;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.utils.Align;

import java.util.*;

import static com.aspectgaming.common.data.GameConst.getSelectionPositions;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.*;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.run;

public class ProgressiveReelAnimComponent extends DrawableComponent {
    private int numCols;
    private int numRows;

    private Image imgRedSbOverlay[];
    private Image imgGreenSbOverlay[];
    private Image imgProSymbolFrame[];
    private Image imgRedReels;
    private Image imgRedReel5;
    private Image imgPressSpin;

    private Sound sndAnticipation;
    private Sound sndPaylinesLp;

    private Video vi5OfaKind = null;
    private Video viProgressive = null;

    private Map<Integer, List<Image>> imgProPayLine = new HashMap<>();
    private Map<Integer, TextureLabel> textProNumLabel = new HashMap<>();
    private List paylineNum = new ArrayList();

    public ProgressiveReelAnimComponent() {
//        setTouchable(Touchable.enabled);

//        init();

//        registerEvent(new GameResetEvent() {
//            @Override
//            public void execute(Object... obj) {
//                clearActions();
//                endAllSpin();
//            }
//        });
//
//        registerEvent(new LanguageChangedEvent() {
//            @Override
//            public void execute(Object... obj) {
//                onLanguageChanged();
//            }
//        });
//
//        registerEvent(new InTiltEvent() {
//            @Override
//            public void execute(Object... obj) {
//                pause();
//            }
//        });
//
//        registerEvent(new OutTiltEvent() {
//            @Override
//            public void execute(Object... obj) {
//                resume();
//            }
//        });
//
//        registerEvent(new ReelStartSpinEvent() {
//            @Override
//            public void execute(Object... obj) {
//                if (vi5OfaKind != null) {
//                    removeActor(vi5OfaKind);
//                    vi5OfaKind = null;
//                }
//
//                if (viProgressive != null) {
//                    removeActor(viProgressive);
//                    viProgressive = null;
//                }
//            }
//        });
    }

    private void init() {
//        numCols = GameConfiguration.getInstance().reel.reels.length;
//        numRows = GameData.getInstance().Context.Result.Stops.length / numCols;
//
//        sndAnticipation = SoundLoader.getInstance().get("progressive/Anticipation");
//        sndPaylinesLp = SoundLoader.getInstance().get("progressive/PayLinesLp");
//
//        imgRedReels = ImageLoader.getInstance().load("Progressive/SpinRemaining/RedReels", "RedReels");
//        imgRedReels.setAlpha(0);
//        addActor(imgRedReels);
//
//        imgRedReel5 = ImageLoader.getInstance().load("Progressive/SpinRemaining/RedReel5", "RedReel5");
//        imgRedReel5.setAlpha(0);
//        addActor(imgRedReel5);
//
//        imgRedSbOverlay = new Image[numCols * numRows];
//        imgGreenSbOverlay = new Image[numRows * numCols];
//        for (int i = 0; i < numCols * numRows; i ++) {
//            int tmpCol = i % numCols;
//            int tmpRow = i / numCols;
//            Vector2 point = CoordinateLoader.getInstance().getPos("Symbol" + tmpCol);
//
//            imgRedSbOverlay[i] = ImageLoader.getInstance().load("Progressive/SpinRemaining/RedSymbolOverlay");
//            imgRedSbOverlay[i].setPosition(point.x, 1080 - (point.y + 210 * (tmpRow + 1)));
//            imgRedSbOverlay[i].setAlpha(0.0f);
//            addActor(imgRedSbOverlay[i]);
//
//            imgGreenSbOverlay[i] = ImageLoader.getInstance().load("Progressive/SpinRemaining/GreenSymbolOverlay");
//            imgGreenSbOverlay[i].setPosition(point.x, 1080 - (point.y + 210 * (tmpRow + 1)));
//            imgGreenSbOverlay[i].setAlpha(0.0f);
//            addActor(imgGreenSbOverlay[i]);
//        }
//
//        imgProSymbolFrame = new Image[numRows];
//        for (int i = 0; i < numRows; i++) {
//
//            Vector2 point = CoordinateLoader.getInstance().getPos("Symbol" + (numCols - 1));
//            imgProSymbolFrame[i] = ImageLoader.getInstance().load("Progressive/SpinRemaining/ProSymbolFrame");
//            imgProSymbolFrame[i].setPosition(point.x, 1080 - (point.y + 210 * (i + 1)));
//            imgProSymbolFrame[i].setAlpha(0.0f);
//            addActor(imgProSymbolFrame[i]);
//        }
    }

    private void onLanguageChanged() {
//        if (imgPressSpin != null) {
//            ImageLoader.getInstance().reload(imgPressSpin);
//        }
    }

    public void play5OfaKind() {
//        if (vi5OfaKind != null) {
//            removeActor(vi5OfaKind);
//            vi5OfaKind = null;
//        }
//        vi5OfaKind = VideoLoader.Instance.load("Progressive/5ofaKind", false);
//        Vector2 point = CoordinateLoader.getInstance().getCoordinate(vi5OfaKind, "OfaKind5");
//        vi5OfaKind.setPosition(point.x, point.y);
//        vi5OfaKind.setAutoVisible(true);
//        addActor(vi5OfaKind);
//
//        vi5OfaKind.play();
//        addAction(delay(3.0f, run(() -> showRetPorPaylineByDelay())));
    }

    public void playJackPot(int level) {
//        if (level > 0 && level <= numRows) {
//            if (viProgressive != null) {
//                removeActor(viProgressive);
//                viProgressive = null;
//            }
//            viProgressive = VideoLoader.Instance.load("Progressive/" + (level - 1), false);
//            Vector2 point = CoordinateLoader.getInstance().getCoordinate(viProgressive, "JackPotAnim" + (level - 1));
//            viProgressive.setPosition(point.x, point.y);
//            viProgressive.setAutoVisible(true);
//            addActor(viProgressive);
//            viProgressive.play();
//        }
    }

    private void addProPayline(int line) {
//        SettingData cfg = GameData.getInstance().Setting;
//        int[][] lines = GameConst.getSelectionPositions(cfg.MaxSelections, cfg.SelectedGame);
//
//        List<Image> proPayLine = new ArrayList<>();
//        if (line >= 0 && lines != null) {
//            int[] positions = lines[line];
//
//            for (int reel = 0; reel < GameConfiguration.getInstance().reel.reels.length - 1; reel++) {
//                int distance = 0;
//                int sum = positions[reel];
//                distance = Math.abs(positions[reel+1] - positions[reel]);
//                sum += positions[reel+1];
//
//                Vector2 pos = CoordinateLoader.getInstance().getPos("IdentifyLine"+sum);
//
//                Image LineFrame0 = ImageLoader.getInstance().load("PayLine/"+distance + "_0");
//                LineFrame0.setColor(GameConfiguration.getInstance().payLine.getLine(line).getColor());
//                LineFrame0.setPosition(pos.x-LineFrame0.getWidth()/2, pos.y-LineFrame0.getHeight()/2);
//                //LineFrame0.setVisible(false);
//                addActor(LineFrame0);
//                proPayLine.add(LineFrame0);
//
//                Image LineFrame1 = ImageLoader.getInstance().load("PayLine/"+distance + "_1");
//                LineFrame1.setPosition(pos.x-LineFrame1.getWidth()/2, pos.y-LineFrame1.getHeight()/2);
//                //LineFrame1.setVisible(false);
//                addActor(LineFrame1);
//                proPayLine.add(LineFrame1);
//            }
//
//            Rectangle bound = CoordinateLoader.getInstance().getBound("LineNumber"+positions[GameConfiguration.getInstance().reel.reels.length - 1]);
//            Image NumFrame0 = ImageLoader.getInstance().load("PayLine/PaylineNumber" + "_0");
//            NumFrame0.setPosition(bound.x - 37, bound.y);
//            NumFrame0.setColor(GameConfiguration.getInstance().payLine.getLine(line).getColor());
//            //NumFrame0.setVisible(false);
//            addActor(NumFrame0);
//            proPayLine.add(NumFrame0);
//
//            Image NumFrame1 = ImageLoader.getInstance().load("PayLine/PaylineNumber" + "_1");
//            NumFrame1.setPosition(bound.x - 37, bound.y);
//            //NumFrame1.setVisible(false);
//            addActor(NumFrame1);
//            proPayLine.add(NumFrame1);
//
//            TextureLabel NumLabel = new TextureLabel("PayLineFont", Align.center, Align.center);
//            NumLabel.setBounds(bound.x, bound.y - 23, bound.width,bound.height);
//            NumLabel.setValue(line+1);
//            //NumLabel.setVisible(false);
//            addActor(NumLabel);
//
//            textProNumLabel.put(line, NumLabel);
//            imgProPayLine.put(line, proPayLine);
//        }
    }

//    private void getRetProPayline(List paylineNum) {
//        if (paylineNum.size() == 0) return;
//
//        if (imgProPayLine.size() > paylineNum.size()) {
//             for (int i = 0; i < paylineNum.size(); i ++) {
//                 for (Integer line : imgProPayLine.keySet()) {
//                     if (line != paylineNum.get(i)) {
//
//                         imgProPayLine.remove(line);
//                     } else {
//                         break;
//                     }
//                 }
//             }
//        }
//    }

    private void clearProPayline() {
//        for (Integer line : imgProPayLine.keySet()) {
//            List <Image> imgPayline = imgProPayLine.get(line);
//            for (Image img : imgPayline) {
//                img.setVisible(false);
//                removeActor(img);
//            }
//            imgPayline.clear();
//
//            TextureLabel labTextNum = textProNumLabel.get(line);
//            labTextNum.setVisible(false);
//            removeActor(labTextNum);
//        }
//
//        imgProPayLine.clear();
//        textProNumLabel.clear();
//        paylineNum.clear();
//    }
//
//    private void showAntPorPayline(boolean isvisible) {
//        for (Integer line : imgProPayLine.keySet()) {
//            List <Image> imgPayline = imgProPayLine.get(line);
//            for (Image img : imgPayline) {
//                img.setVisible(isvisible);
//            }
//
//            TextureLabel labTextNum = textProNumLabel.get(line);
//            labTextNum.setVisible(isvisible);
//        }
    }

    private void showRetPorPaylineByDelay() {
//        sndPaylinesLp.play();
//        for(int i = 0; i < paylineNum.size(); i ++) {
//            int lineIndex = i;
//            for (Integer line : imgProPayLine.keySet()) {
//                if (line.equals(paylineNum.get(i))) {
//                    float delay = (float)Math.log(i + 1);
//                    addAction(delay(delay, run( () -> {
//                        List <Image> imgPayline = imgProPayLine.get(line);
//                        for (Image img : imgPayline) {
//                            img.setVisible(true);
//                        }
//
//                        TextureLabel labTextNum = textProNumLabel.get(line);
//                        labTextNum.setVisible(true);
//
//                        if (lineIndex == paylineNum.size()-1){
//                            sndPaylinesLp.stop();
//                        }
//                    })));
//                }
//            }
//        }
    }

    public void showRetPorPayline() {
//        clearActions();
//        for(int i = 0; i < paylineNum.size(); i ++) {
//            for (Integer line : imgProPayLine.keySet()) {
//                if (line.equals(paylineNum.get(i))) {
//                    List <Image> imgPayline = imgProPayLine.get(line);
//                    for (Image img : imgPayline) {
//                        img.setVisible(true);
//                    }
//
//                    TextureLabel labTextNum = textProNumLabel.get(line);
//                    labTextNum.setVisible(true);
//                }
//            }
//        }
    }

    public boolean isProAntiPos(int stopIndex) {
        return  false;
    }


    public void showAnticipation() {
    }

    public void showResult() {

    }

    public void showProSymbolFrame(boolean show, int spinedCount, int[]jackPotLevelInfo) {
//        if (!show) {
//            for (int i = 0; i < numRows; i ++) {
//                imgProSymbolFrame[i].setAlpha(0.0f);
//            }
//        } else {
//            int totalCount = 0;
//            for (int i = 0; i < jackPotLevelInfo.length; i++) {
//                if (jackPotLevelInfo[i] != 0) {
//                    if (totalCount < spinedCount) {
//                        imgGreenSbOverlay[(i + 1) * numCols - 1].setAlpha(0.0f);
//                        imgProSymbolFrame[i].setAlpha(0.0f);
//                    } else if (totalCount == spinedCount) {
//                        imgGreenSbOverlay[(i + 1) * numCols - 1].setAlpha(0.0f);
//                        imgProSymbolFrame[i].setAlpha(1.0f);
//                    } else {
//                        imgProSymbolFrame[i].setAlpha(0.0f);
//                    }
//
//                    totalCount++;
//                }
//            }
//        }
    }

//    public void showSpingRemaining(boolean show, int spinedCount, int[] jackPotLevelInfo) {
//        if (show) {
//            int totalCount = 0;
//            for (int i = 0; i < jackPotLevelInfo.length; i++) {
//                if (jackPotLevelInfo[i] != 0) {
//                    totalCount++;
//                }
//            }
//            int remaingSpinCount = totalCount - spinedCount;
//            if (remaingSpinCount > 0) {
//                ImageLoader.getInstance().reload(imgRemaingSpin, "Progressive/SpinRemaining/" + remaingSpinCount);
//                imgRemaingSpin.setAlpha(1.0f);
//            }
//        } else {
//            imgRemaingSpin.setAlpha(0.0f);
//        }
//    }

//    public void set5ofaKindTicker(boolean isVisible) {
//        if (isVisible) {
//            String message = MessageLoader.getInstance().getMessage("5ofaKindTicker");
//            lab5ofaKindTicker.setText(message);
//        } else {
//            lab5ofaKindTicker.setText("");
//        }
//    }

    public void setPressSpin(boolean isVisible) {
//        if (imgPressSpin == null) {
//            imgPressSpin = ImageLoader.getInstance().load("Progressive/SpinRemaining/PressSpin");
//            Vector2 point = CoordinateLoader.getInstance().getPos("PressSpin");
//            imgPressSpin.setPosition(point.x - 360, 1080 - (point.y + imgPressSpin.getHeight() - 130));
//
//            addActor(imgPressSpin);
//        }
//
//        imgPressSpin.setVisible(isVisible);
    }

    public void setRedReelsVisible(boolean isVisible) {
//        if (isVisible) {
//            imgRedReels.addAction(fadeIn(1.0f));
//            imgRedReel5.addAction(fadeIn(1.0f));
//        } else {
//            imgRedReels.addAction(fadeOut(1.0f));
//            imgRedReel5.addAction(fadeOut(1.0f));
//        }
    }

    public void endAllSpin() {
//        clearActions();
//        if (imgPressSpin != null) {
//            imgPressSpin.setVisible(false);
//            removeActor(imgPressSpin);
//            imgPressSpin = null;
//        }
//
//        setRedReelsVisible(false);
//
//        clearProPayline();
//        for (int i = 0; i < numCols * numRows; i ++) {
//            imgRedSbOverlay[i].clearActions();
//            imgRedSbOverlay[i].setAlpha(0.0f);
//            imgGreenSbOverlay[i].setAlpha(0.0f);
//        }
    }

    public boolean isTriProAnti() {
        return false;
    }
}
