package amrish.ravidas.com.alpha;

import android.util.SparseArray;

import androidx.annotation.NonNull;

import static amrish.ravidas.com.alpha.GameState.CellPosition.A;
import static amrish.ravidas.com.alpha.GameState.CellPosition.B;
import static amrish.ravidas.com.alpha.GameState.CellPosition.C;
import static amrish.ravidas.com.alpha.GameState.CellPosition.D;
import static amrish.ravidas.com.alpha.GameState.CellPosition.E;
import static amrish.ravidas.com.alpha.GameState.CellPosition.F;
import static amrish.ravidas.com.alpha.GameState.CellPosition.G;
import static amrish.ravidas.com.alpha.GameState.CellPosition.H;
import static amrish.ravidas.com.alpha.GameState.CellPosition.I;

class GameState {

    @NonNull private final ViewTicTacToeCell.CellType[][] mBlocks;
    @NonNull private final Player[] mPlayers;
    @NonNull private GameStatus mGameStatus;
    private int mCurrentPlayerIndex;
    private int mStepCount;
    @NonNull private CellPosition[] mWinningCells;

    GameState(Player player1, Player player2) {
        mBlocks = new ViewTicTacToeCell.CellType[3][3];
        for (int i=0; i<mBlocks.length; i++) {
            for (int j=0; j<mBlocks[i].length; j++)
                mBlocks[i][j] = ViewTicTacToeCell.CellType.NONE;
        }
        mGameStatus = GameStatus.InProgress;
        mPlayers = new Player[]{ player1, player2 };
        mCurrentPlayerIndex = 0;
        mStepCount = 0;
        mWinningCells = new CellPosition[3];
    }

    enum GameStatus {
        InProgress,
        HasWinner,
        Draw,
    }

    enum CellPosition {
        A(0, "0X0"),
        B(1, "0X1"),
        C(2, "0x2"),
        D(3, "1X0"),
        E(4, "1X1"),
        F(5, "1x2"),
        G(6, "2X0"),
        H(7, "2X1"),
        I(8, "2x2");

        private String mPosition;
        private int mIndex;
        private static SparseArray<CellPosition> lookupMap;
        CellPosition(int index, String address) {
            this.mIndex = index;
            this.mPosition = address;
        }

        public String getValue() {
            return mPosition;
        }

        static {
            lookupMap = new SparseArray<>();
            for (CellPosition value : CellPosition.values()) {
                lookupMap.put(value.mIndex, value);
            }
        }

        public static CellPosition valueOf(int index) {
            return lookupMap.get(index);
        }
    }

    @NonNull
    Player getCurrentPlayer() {
        return mPlayers[mCurrentPlayerIndex];
    }

    private int getNextPlayerIndex() {
        return ++mCurrentPlayerIndex % 2;
    }

    @NonNull
    Player getNextPlayer() {
        return mPlayers[getNextPlayerIndex()];
    }

    @NonNull
    ViewTicTacToeCell.CellType[][] getGridState() {
     return mBlocks;
    }

    @NonNull
    GameStatus getStatus() {
        return mGameStatus;
    }

    @NonNull
    CellPosition[] getWinningCells() {
        return mWinningCells;
    }

    @NonNull
    ViewTicTacToeCell.CellType getBlockTypeAtPosition(final CellPosition position) {
        switch (position) {
            case A:
                return mBlocks[0][0];
            case B:
                return mBlocks[0][1];
            case C:
                return mBlocks[0][2];
            case D:
                return mBlocks[1][0];
            case E:
                return mBlocks[1][1];
            case F:
                return mBlocks[1][2];
            case G:
                return mBlocks[2][0];
            case H:
                return mBlocks[2][1];
            case I:
                return mBlocks[2][2];
        }
        throw new UnsupportedOperationException("Invalid position value of " + position);
    }

    void moveNext(final CellPosition cellPosition) {
        // TODO - UPDATE THE PROVIDED BLOCK INDEX
        // TODO - CHECK IF THE GAME HAS WINNER/DRAW
        // TODO -   IN PROGRESS - UPDATE NEXT AND CURRENT PLAYER
        // TODO -   WINNER - UPDATE THE WINNER
        // TODO -   DRAW - COMPLETE GAME
        if (getBlockTypeAtPosition(cellPosition) != ViewTicTacToeCell.CellType.NONE) {
            throw new IllegalStateException();
        }
        final ViewTicTacToeCell.CellType cellType = getCurrentPlayer().getPlayerBlockType();
        switch (cellPosition) {
            case A:
                mBlocks[0][0] = cellType;
                break;
            case B:
                mBlocks[0][1] = cellType;
                break;
            case C:
                mBlocks[0][2] = cellType;
                break;
            case D:
                mBlocks[1][0] = cellType;
                break;
            case E:
                mBlocks[1][1] = cellType;
                break;
            case F:
                mBlocks[1][2] = cellType;
                break;
            case G:
                mBlocks[2][0] = cellType;
                break;
            case H:
                mBlocks[2][1] = cellType;
                break;
            case I:
                mBlocks[2][2] = cellType;
                break;
        }
        mCurrentPlayerIndex = getNextPlayerIndex();
        mStepCount += 1;

        // Check for winners
        boolean[] mHorizontal = new boolean[]{true, true, true};
        boolean[] mVertical = new boolean[]{true, true, true};
        for (int i=0; i<3; i++) {
            for (int j=0; j<3; j++) {
                if (mBlocks[i][j] == ViewTicTacToeCell.CellType.NONE) {
                    mHorizontal[i] = false;
                    mVertical[j] = false;
                    continue;
                }
                // Check for winner row-wise
                if (mHorizontal[i] && j > 0 && mBlocks[i][j] != mBlocks[i][j-1]) {
                    mHorizontal[i] = false;
                }

                // Check for winner column-wise
                if (mVertical[j] && i > 0 && mBlocks[i][j] != mBlocks[i-1][j]) {
                    mVertical[j] = false;
                }
            }
        }

        for (int i=0; i<mHorizontal.length; i++) {
            if (mHorizontal[i]) {
                mGameStatus = GameStatus.HasWinner;
                mWinningCells = getCellsInRow(i);
                break;
            }
        }

        if (mGameStatus != GameStatus.HasWinner) {
            for (int i=0; i<mVertical.length; i++) {
                if (mVertical[i]) {
                    mGameStatus = GameStatus.HasWinner;
                    mWinningCells = getCellsInColumn(i);
                    break;
                }
            }
        }

        if (mStepCount == 9 && mGameStatus != GameStatus.HasWinner) {
            mGameStatus = GameStatus.Draw;
        }
    }

    private CellPosition[] getCellsInRow(int row) {
        switch (row) {
            case 0:
                return new CellPosition[]{A, B, C};
            case 1:
                return new CellPosition[]{D, E, F};
            default:
                return new CellPosition[]{G, H, I};
        }
    }

    private CellPosition[] getCellsInColumn(int column) {
        switch (column) {
            case 0:
                return new CellPosition[]{A, D, G};
            case 1:
                return new CellPosition[]{B, E, H};
            default:
                return new CellPosition[]{G, F, I};
        }
    }
}
