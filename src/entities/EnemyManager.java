package entities;

import java.awt.Graphics;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

import gamestates.Playing;
import levels.Level;
import utilz.LoadSave;
import static utilz.Constants.EnemyConstants.*;

public class EnemyManager {

	private Playing playing;
	private BufferedImage[][] pigArr, kingPigArr, sharkArr;
	private Level currentLevel;

	public EnemyManager(Playing playing) {
		this.playing = playing;
		loadEnemyImgs();
	}

	public void loadEnemies(Level level) {
		this.currentLevel = level;
	}

	public void update(int[][] lvlData) {
		boolean isAnyActive = false;
		for (Pig c : currentLevel.getPigs())
			if (c.isActive()) {
				c.update(lvlData, playing);
				isAnyActive = true;
			}

		for (KingPig p : currentLevel.getKingPigs())
			if (p.isActive()) {
				p.update(lvlData, playing);
				isAnyActive = true;
			}

		for (Shark s : currentLevel.getSharks())
			if (s.isActive()) {
				s.update(lvlData, playing);
				isAnyActive = true;
			}

		if (!isAnyActive)
			playing.setLevelCompleted(true);
	}

	public void draw(Graphics g, int xLvlOffset) {
		drawPigs(g, xLvlOffset);
		drawKingPigs(g, xLvlOffset);
		drawSharks(g, xLvlOffset);
	}

	private void drawSharks(Graphics g, int xLvlOffset) {
		for (Shark s : currentLevel.getSharks())
			if (s.isActive()) {
				g.drawImage(sharkArr[s.getState()][s.getAniIndex()], (int) s.getHitbox().x - xLvlOffset - SHARK_DRAWOFFSET_X + s.flipX(),
						(int) s.getHitbox().y - SHARK_DRAWOFFSET_Y + (int) s.getPushDrawOffset(), SHARK_WIDTH * s.flipW(), SHARK_HEIGHT, null);
//				s.drawHitbox(g, xLvlOffset);
//				s.drawAttackBox(g, xLvlOffset);
			}
	}

	private void drawKingPigs(Graphics g, int xLvlOffset) {
		for (KingPig p : currentLevel.getKingPigs())
			if (p.isActive()) {
				g.drawImage(kingPigArr[p.getState()][p.getAniIndex()], (int) p.getHitbox().x - xLvlOffset - KINGPIG_DRAWOFFSET_X + p.flipX(),
						(int) p.getHitbox().y - KINGPIG_DRAWOFFSET_Y + (int) p.getPushDrawOffset(), KINGPIG_WIDTH * p.flipW(), KINGPIG_HEIGHT, null);
//				p.drawHitbox(g, xLvlOffset);
			}
	}

	private void drawPigs(Graphics g, int xLvlOffset) {
		for (Pig c : currentLevel.getPigs())
			if (c.isActive()) {

				g.drawImage(pigArr[c.getState()][c.getAniIndex()], (int) c.getHitbox().x - xLvlOffset - PIG_DRAWOFFSET_X + c.flipX(),
						(int) c.getHitbox().y - PIG_DRAWOFFSET_Y + (int) c.getPushDrawOffset(), PIG_WIDTH * c.flipW(), PIG_HEIGHT, null);

//				c.drawHitbox(g, xLvlOffset);
//				c.drawAttackBox(g, xLvlOffset);
			}

	}

	public void checkEnemyHit(Rectangle2D.Float attackBox) {
		for (Pig p : currentLevel.getPigs())
			if (p.isActive())
				if (p.getState() != DEAD && p.getState() != HIT)
					if (attackBox.intersects(p.getHitbox())) {
						p.hurt(20);
						return;
					}

		for (KingPig p : currentLevel.getKingPigs())
			if (p.isActive()) {
				if (p.getState() == ATTACK && p.getAniIndex() >= 3)
					return;
				else {
					if (p.getState() != DEAD && p.getState() != HIT)
						if (attackBox.intersects(p.getHitbox())) {
							p.hurt(20);
							return;
						}
				}
			}

		for (Shark s : currentLevel.getSharks())
			if (s.isActive()) {
				if (s.getState() != DEAD && s.getState() != HIT)
					if (attackBox.intersects(s.getHitbox())) {
						s.hurt(20);
						return;
					}
			}
	}

	private void loadEnemyImgs() {
		pigArr = getImgArr(LoadSave.GetSpriteAtlas(LoadSave.PIG_SPRITE), 9, 5, PIG_WIDTH_DEFAULT, PIG_HEIGHT_DEFAULT);
		kingPigArr = getImgArr(LoadSave.GetSpriteAtlas(LoadSave.KINGPIG_ATLAS), 8, 5, KINGPIG_WIDTH_DEFAULT, KINGPIG_HEIGHT_DEFAULT);
		sharkArr = getImgArr(LoadSave.GetSpriteAtlas(LoadSave.SHARK_ATLAS), 8, 5, SHARK_WIDTH_DEFAULT, SHARK_HEIGHT_DEFAULT);
	}

	private BufferedImage[][] getImgArr(BufferedImage atlas, int xSize, int ySize, int spriteW, int spriteH) {
		BufferedImage[][] tempArr = new BufferedImage[ySize][xSize];
		for (int j = 0; j < tempArr.length; j++)
			for (int i = 0; i < tempArr[j].length; i++)
				tempArr[j][i] = atlas.getSubimage(i * spriteW, j * spriteH, spriteW, spriteH);
		return tempArr;
	}

	public void resetAllEnemies() {
		for (Pig c : currentLevel.getPigs())
			c.resetEnemy();
		for (KingPig p : currentLevel.getKingPigs())
			p.resetEnemy();
		for (Shark s : currentLevel.getSharks())
			s.resetEnemy();
	}

}
