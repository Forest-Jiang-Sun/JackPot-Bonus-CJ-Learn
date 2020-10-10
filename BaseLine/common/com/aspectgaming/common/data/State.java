package com.aspectgaming.common.data;

/**
 * @author ligang.yao
 */
public interface State {
    int VerifyingSignature           = 1;
    int CheckingCriticalMemory       = 2;
    int Handpay                      = 3;
    int Tilt                         = 4;
    int OutOfService                 = 5;
    int Reserve                      = 6;
    int DisabledByOnlineSystem       = 7;
    int AttendantApp                 = 8;
    int CalibrateTouchscreen         = 9;
    int BillAccepting                = 10;
    int CashoutPending               = 11;
    int TicketPrinting               = 12;
    int DelayGame                    = 13;
    int AFTLock                      = 14;

    /* Base Game States */
    int GameIdle                     = 15;
    int PrimaryGameStarted           = 16;
    int PayGameResults               = 17;
    int ReelStop                     = 18;
    int GambleChoice                 = 19;

    /* Game States */
    int GambleStarted                = 21;
    int GambleDisplayPending         = 22;
    int GambleWin                    = 23;
    
    int StartFreeSpin                = 24;
    int FreeGameIntro                = 25;
    int FreeGameOutro                = 26;
    int FreeGameStarted              = 27;
    int FreeGameResults              = 28;

    int BonusActive                  = 29;
    int BonusDisplayPending          = 30;

    int ProgressiveIntro             = 46;
    int ProgressiveStarted           = 32;
    int AwardSASProgressive          = 31;
    int ProgressiveResults           = 33;

    int SelectBet                    = 20;
    int SelectGame                   = 36;

    int HOST_CASHOUT_PENDING         = 34;
}
