using SimpleJSON;
using System;
using System.Collections.Generic;

namespace BanCa.Libs
{
    public class Config
    {
        static Config()
        {
            var json = JSON.Parse("{"TableStartingBlinds":[1,10000,100000,1000000],"TableBulletValueRate":[1,1,10,100],"TableRequireCardIn":[0,0,0,0],"NumberOfAccountPerDevice":1000,"NumberOfAccountPerDay":3,"RequestSampleRateMs":2000,"MaxRequestPerSecond":15,"LogShooting":1,"MinTimeBetweenShooting":125,"TimeBetweenVideoAdsMs0":10000,"TimeBetweenVideoAdsMs1":10000,"VideoAdsRewardCount0":3,"VideoAdsRewardCount1":5,"VideoAdsRewardType0":100,"VideoAdsRewardType1":50,"KickDuplicateUsers":1,"HideFishHp":1,"PowerUpIntervalS":300,"PowerUpMinPlayTimeS":120,"PowerUpDurationS":15,"BombRate":0,"BonusRate":45,"FastFireCoolDownS":1,"FastFireRate":2,"FastFireDuration":10,"FastFireCost":20,"SnipeCoolDownS":1,"SnipeDurationS":10,"SnipeCost":20,"FeeRate":0.02,"BombThreshold":5000,"JackpotInitial":7300,"JackpotRate":0.02,"FIRE_RATE":4,"TURN_TIME":0.25,"MIN_SPEED_2":0.01,"MAX_JUMP_SPEED":150,"MAX_JUMP_SPEED_2":22500,"MAX_SHADOW_SPEED_2":25600,"TELEPORT_SHADOW_SPEED_2":250000,"JUMP_ACCELERATION_OCTOPUS":150,"JUMP_DECCELERATION_OCTOPUS":150,"JUMP_ACCELERATION_CUTTLE":150,"JUMP_DECCELERATION_CUTTLE":150,"JUMP_ACCELERATION_SEA_TURTLE":150,"JUMP_DECCELERATION_SEA_TURTLE":150,"MIN_SPEED":40,"MAX_VARY_SPEED":80,"TURN_ANGLE_RAD":0.01744444,"JUMP_TURN_ANGLE_RAD":0.01744444,"MAX_BOUND_TIME":4,"BulletSpeed":1400,"BulletRadius":22,"MaxIdleTimeS":120,"a":8,"b":32,"c":1024,"d":2048,"MinScale":1,"MaxScale":1.05,"MaxFillScale":1.2,"NUMBER_OF_BULLETS":100,"NUMBER_OF_OBJECTS":60,"PLAYING_WAVE_DURATION":600,"NEW_WAVE_MAX_TIME":120,"SOLO_DURATION":120,"WAITING_WAVE_DURATION":30,"GUN_LENGTH":120,"GOLDEN_FROG_WIN_RATE":[1,5,12,16,10],"GOLDEN_FROG_WIN_MULTIPLE":[5,4,3,2,0.5],"ScreenW":1280,"ScreenH":720,"ScreenX":-640,"ScreenY":-360,"WorldW":2400,"WorldH":1700,"WorldX":-1200,"WorldY":-850,"OutWorldW":5000,"OutWorldH":4000,"OutWorldX":-2500,"OutWorldY":-2000,"SpawnW":2000,"SpawnH":1600,"SpawnX":-1000,"SpawnY":-800,"SERVER_UPDATE_LOOP_MS":17,"CLIENT_UPDATE_S":0.033,"playerPos":[{"x":-320,"y":-320},{"x":320,"y":-320},{"x":320,"y":320},{"x":-320,"y":320}],"TypeToPower":{"Basic":1,"Bullet1":100,"Bullet2":100,"Bullet3":100,"Bullet4":100,"Bullet5":100,"Bullet6":100},"TypeToValue":{"Basic":0,"Bullet1":10,"Bullet2":20,"Bullet3":50,"Bullet4":100,"Bullet5":200,"Bullet6":500},"FishPhysicalData":{"Basic":{"Width":2,"Height":2,"Health":0,"HealthRate":1,"HealthScale":0.2},"Cuttle":{"Width":22,"Height":46,"Health":200,"HealthRate":1,"HealthScale":0.1},"GoldFish":{"Width":29,"Height":49,"Health":300,"HealthRate":1,"HealthScale":0.1},"LightenFish":{"Width":22,"Height":46,"Health":400,"HealthRate":0.9,"HealthScale":0.1},"Mermaid":{"Width":33,"Height":45,"Health":500,"HealthRate":0.95,"HealthScale":0.1},"Octopus":{"Width":32,"Height":61,"Health":600,"HealthRate":1,"HealthScale":0.1},"PufferFish":{"Width":27,"Height":41,"Health":700,"HealthRate":0.8,"HealthScale":0.1},"SeaFish":{"Width":30,"Height":58,"Health":800,"HealthRate":0.8,"HealthScale":0.2},"Shark":{"Width":46,"Height":61,"Health":900,"HealthRate":0.9,"HealthScale":0.1},"Stringray":{"Width":37,"Height":82,"Health":1000,"HealthRate":0.95,"HealthScale":0.1},"Turtle":{"Width":26,"Height":66,"Health":1100,"HealthRate":0.7,"HealthScale":0.1},"CaThanTai":{"Width":28,"Height":64,"Health":1200,"HealthRate":1,"HealthScale":0.2},"FlyingFish":{"Width":35,"Height":72,"Health":1300,"HealthRate":0.9,"HealthScale":0.1},"GoldenFrog":{"Width":100,"Height":436,"Health":6600,"HealthRate":1,"HealthScale":0.1},"SeaTurtle":{"Width":100,"Height":50,"Health":1400,"HealthRate":1,"HealthScale":0.1},"MerMan":{"Width":46,"Height":62,"Health":1500,"HealthRate":1,"HealthScale":0.1},"Phoenix":{"Width":61,"Height":63,"Health":1600,"HealthRate":1,"HealthScale":0.1},"MermaidBig":{"Width":100,"Height":88,"Health":1700,"HealthRate":0.98,"HealthScale":0.1},"MermaidSmall":{"Width":40,"Height":143,"Health":1800,"HealthRate":1,"HealthScale":0.1},"BombFish":{"Width":61,"Height":175,"Health":5400,"HealthRate":1,"HealthScale":0.1},"Fish19":{"Width":46,"Height":175,"Health":6000,"HealthRate":1,"HealthScale":0.1},"Fish20":{"Width":50,"Height":145,"Health":6300,"HealthRate":1,"HealthScale":0.1},"Fish21":{"Width":56,"Height":175,"Health":6600,"HealthRate":1,"HealthScale":0.1},"Fish22":{"Width":63,"Height":187,"Health":6900,"HealthRate":1,"HealthScale":0.1},"Fish23":{"Width":100,"Height":261,"Health":7200,"HealthRate":1,"HealthScale":0.1},"Fish24":{"Width":100,"Height":301,"Health":7500,"HealthRate":1,"HealthScale":0.1},"Fish25":{"Width":100,"Height":301,"Health":8500,"HealthRate":1,"HealthScale":0.1}},"FakeJpMinTimeMS":{"11":300000,"12":300000,"13":300000,"14":300000,"21":600000,"22":600000,"23":600000,"24":600000,"31":3600000,"32":3600000,"33":3600000,"34":3600000},"FakeJpMaxTimeMS":{"11":1800000,"12":1800000,"13":1800000,"14":1800000,"21":3600000,"22":3600000,"23":3600000,"24":3600000,"31":21600000,"32":21600000,"33":21600000,"34":21600000},"MinAddFakeJp":{"11":100,"12":100,"13":100,"14":100,"21":300,"22":300,"23":300,"24":300,"31":200,"32":200,"33":200,"34":200},"MaxAddFakeJp":{"11":500,"12":500,"13":500,"14":500,"21":1500,"22":1500,"23":1500,"24":1500,"31":1000,"32":1000,"33":1000,"34":1000},"SoloFee":0.05,"SoloSnipe":3,"SoloFastFire":3,"SoloBomb":3,"SoloItemBombDamage":300,"TableSoloCashIn":[1,5000,50000,500000],"TableSoloVirtualCash":[1,5000,50000,500000],"UserCardInOnOff":1,"UserHighCash":20000,"UserMidCash":10000,"UserCardInHighCash":20000,"UserCardInMidCash":10000,"MinimumRefundUserBankRate":[1.3,1.2,1.1],"MinimumRefundUserCardInBankRate":[1.2,1.1,1]}");
            Config.ParseJson(json);
        }

        public static Action OnConfigChange;

        public static volatile bool IsMaintain = false;

        public static volatile int NumberOfAccountPerDevice = 5;
        public static volatile int NumberOfAccountPerDay = 3;

        public static volatile int RequestSampleRateMs = 2000;
        public static volatile float MaxRequestPerSecond = 20f;
        public static volatile int LogShooting = 1;
        public static volatile int MinTimeBetweenShooting = 125;

        public static volatile int TimeBetweenVideoAdsMs0 = 10 * 1000;
        public static volatile int TimeBetweenVideoAdsMs1 = 10 * 1000;
        public static volatile int VideoAdsRewardType0 = 50;
        public static volatile int VideoAdsRewardCount0 = 3;
        public static volatile int VideoAdsRewardType1 = 10;
        public static volatile int VideoAdsRewardCount1 = 5;
        public static volatile int KickDuplicateUsers = 1;
        public static volatile int HideFishHp = 1;

        #region Table
        public static volatile long[] TableStartingBlinds = new long[] { 1, 1000, 10000, 100000 };
        public static volatile int[] TableBulletValueRate = new int[] { 1, 1, 10, 100 };
        public static volatile long[] TableRequireCardIn = new long[] { 0, 0, 10000, 20000 };

        // World
        public volatile static float NUMBER_OF_BULLETS = 100;
        public volatile static float NUMBER_OF_OBJECTS = 60; //60
        public volatile static float NEW_WAVE_MAX_TIME = 120f;
        public volatile static float SOLO_DURATION = 120f;
        public volatile static float PLAYING_WAVE_DURATION = 600f; //10f * 60; // 2592000; // a month :) // 15f * 60; // time between wave
        public volatile static float WAITING_WAVE_DURATION = 30; // //30;
        public volatile static float GUN_LENGTH = 120;
        public volatile static Vector[] playerPos = new Vector[] { new Vector(-320, -320), new Vector(320, -320), new Vector(320, 320), new Vector(-320, 320) };

        public volatile static float ScreenW = 1280, ScreenH = 720, ScreenX = -ScreenW / 2, ScreenY = -ScreenH / 2;
        public volatile static float WorldW = 2400, WorldH = 1700, WorldX = -WorldW / 2, WorldY = -WorldH / 2;
        public volatile static float OutWorldW = 5000, OutWorldH = 4000, OutWorldX = -OutWorldW / 2, OutWorldY = -OutWorldH / 2;
        public volatile static float SpawnW = 2000, SpawnH = 1600, SpawnX = -SpawnW / 2, SpawnY = -SpawnH / 2;

        public volatile static int SERVER_UPDATE_LOOP_MS = 17;
        public volatile static float CLIENT_UPDATE_S = 0.033f;

        public volatile static Dictionary<int, int> FakeJpMinTimeMS = new Dictionary<int, int>
            { {11, 5 * 60 * 1000}, {12, 5 * 60 * 1000}, {13, 5 * 60 * 1000}, {14, 5 * 60 * 1000},
              {21, 10 * 60 * 1000}, {22, 10 * 60 * 1000}, {23, 10 * 60 * 1000}, {24, 10 * 60 * 1000},
              {31, 60 * 60 * 1000},{32, 60 * 60 * 1000},{33, 60 * 60 * 1000},{34, 60 * 60 * 1000},
        };
        public volatile static Dictionary<int, int> FakeJpMaxTimeMS = new Dictionary<int, int>
            { {11, 30 * 60 * 1000},{12, 30 * 60 * 1000},{13, 30 * 60 * 1000},{14, 30 * 60 * 1000},
              {21, 60 * 60 * 1000},{22, 60 * 60 * 1000},{23, 60 * 60 * 1000},{24, 60 * 60 * 1000},
              {31, 6 * 60 * 60 * 1000},{32, 6 * 60 * 60 * 1000},{33, 6 * 60 * 60 * 1000},{34, 6 * 60 * 60 * 1000},
        };
        public volatile static Dictionary<int, int> MinAddFakeJp = new Dictionary<int, int>
            { {11, 100},{12, 100},{13, 100},{14, 100},
              {21, 300},{22, 300},{23, 300},{24, 300},
              {31, 200},{32, 200},{33, 200},{34, 200},
        };
        public volatile static Dictionary<int, int> MaxAddFakeJp = new Dictionary<int, int>
            { {11, 500},{12, 500},{13, 500},{14, 500},
              {21, 1500},{22, 1500},{23, 1500},{24, 1500},
              {31, 1000},{32, 1000},{33, 1000},{34, 1000},
        };

        /// starting gun sprite, -2 (out of money), -1 (too much money)
        public static int GetStartingBulletType(int index)
        {
            if (index == -1 || index >= 12)
            {
                return 5;
            }

            if (index < TableBulletValueRate.Length && index >= 0)
            {
                return (index / 2) % 6;
            }

            return 0;
        }

        /// = -2 (out of money), -1 (too much money)
        public static int GetTableBlindIndexForPlayer(long startingCash)
        {
            if (startingCash == 0)
                return -2;

            for (int i = 1; i < TableStartingBlinds.Length; i++)
            {
                if (startingCash <= TableStartingBlinds[i] && startingCash > TableStartingBlinds[i - 1])
                    return i - 1;
            }

            //return -1;
            return TableStartingBlinds.Length - 1;
        }

        /// = -2 (out of money), -1 (too much money)
        public static long GetTableBlind(int index)
        {
            if (index == -1)
            {
                return TableStartingBlinds[TableStartingBlinds.Length - 1];
            }

            if (index >= 0 && index < TableStartingBlinds.Length)
            {
                return TableStartingBlinds[index];
            }

            return 0;
        }

        /// index = -2 return 0, index = -1 return max
        public static long GetBulletValue(int index, BulletType type)
        {
            var val = TypeToValue[type];
            if (index == -1)
            {
                var rate = TableBulletValueRate[TableBulletValueRate.Length - 1];
                return (long)rate * val;
            }

            if (index >= 0 && index < TableBulletValueRate.Length)
            {
                var rate = TableBulletValueRate[index];
                return (long)rate * val;
            }

            return 0;
        }
        #endregion

        #region Items
        public static volatile float PowerUpIntervalS = 5 * 60; // power trigger per PowerUpIntervalS second on player with lowest below zero profit with play time > PowerUpMinPlayTimeS
        public static volatile float PowerUpMinPlayTimeS = 2 * 60;
        public static volatile float PowerUpDurationS = 15;
        public static volatile int BombRate = 0;
        public static volatile int BonusRate = 45;

        public static volatile float FastFireCoolDownS = 1;
        public static volatile float FastFireRate = 2;
        public static volatile float FastFireDuration = 10;
        public static volatile int FastFireCost = 20;
        public static volatile float SnipeCoolDownS = 1;
        public static volatile float SnipeDurationS = 10;
        public static volatile int SnipeCost = 20;

        public enum PowerUp : int
        {
            None = -1,
            FreeShot = 0,
            ClearStage = 1,
            Bonus25 = 2,
            FastShoot = 3,
            Auto = 4,
            Snipe = 5
        }
        #endregion

        #region Fish
        public enum MoveType : int
        {
            None = -1, Straight = 0, TurnLeft = 1, TurnRight = 2, Jump = 3, JumpLeft = 4, JumpRight = 5, All = 6
        }

        public enum FishType : int
        {
            Basic = -1,
            Cuttle = 0,
            GoldFish, //1
            LightenFish, //2
            Mermaid, //3
            Octopus, //4
            PufferFish, //5
            SeaFish, //6
            Shark, //7
            Stringray, //8
            Turtle, //9
            CaThanTai, //10
            FlyingFish, //11
            GoldenFrog, //12
            SeaTurtle, //13
            MerMan, //14
            Phoenix, //15
            MermaidBig, //16
            MermaidSmall, //17
            BombFish, //18
            Fish19, //19
            Fish20, //20
            Fish21, //21
            Fish22, //22
            Fish23, //23
            Fish24, //24
            Fish25, //25
            All
        }

        public static volatile float JackpotRate = 0.03f; // amount accumulate to jackpot
        public static volatile float FeeRate = 0.02f;

        public static volatile float BombThreshold = 5000;
        public static volatile float JackpotInitial = 2500;

        public static volatile float FIRE_RATE = 4f;
        public static volatile float TURN_TIME = 0.25f;
        public static volatile float MIN_SPEED_2 = 0.01f;
        public static volatile float MAX_JUMP_SPEED = 150f;
        public static volatile float MAX_JUMP_SPEED_2 = 150f * 150f;
        public static volatile float MAX_SHADOW_SPEED_2 = 160f * 160f;
        public static volatile float TELEPORT_SHADOW_SPEED_2 = 500f * 500f;
        public static volatile float JUMP_ACCELERATION_OCTOPUS = 150f;
        public static volatile float JUMP_DECCELERATION_OCTOPUS = 150f;
        public static volatile float JUMP_ACCELERATION_CUTTLE = 150f; //120
        public static volatile float JUMP_DECCELERATION_CUTTLE = 150f;
        public static volatile float JUMP_ACCELERATION_SEA_TURTLE = 150f; //100
        public static volatile float JUMP_DECCELERATION_SEA_TURTLE = 150f; //200
        public static volatile float MIN_SPEED = 40f;
        public static volatile float MAX_VARY_SPEED = 80f;
        public static volatile float TURN_ANGLE_RAD = 1f * 3.14f / 180;
        public static volatile float JUMP_TURN_ANGLE_RAD = 1f * 3.14f / 180;

        public static volatile List<float> GOLDEN_FROG_WIN_RATE = new List<float> { 1, 5, 12, 16, 10 };
        public static volatile List<float> GOLDEN_FROG_WIN_MULTIPLE = new List<float> { 5, 4, 3, 2, 0.5f };

        public class FishPhysicalInfo
        {
            public volatile float Width, Height, Health;
            public volatile float HealthRate = 1.03f; //3%
            public volatile float HealthScale = 1.5f; //+-40%

            public FishPhysicalInfo(float Width, float Height, float Health, float HealthRate = 1.03f, float HealthScale = 1.5f)
            {
                this.Width = Width;
                this.Height = Height;
                this.Health = Health;
                this.HealthRate = HealthRate;
                this.HealthScale = HealthScale;
            }

            public JSONNode ToJson()
            {
                var data = new JSONObject();
                data["Width"] = Width;
                data["Height"] = Height;
                data["Health"] = Health;
                data["HealthRate"] = HealthRate;
                data["HealthScale"] = HealthScale;
                return data;
            }

            public void ParseJson(JSONNode data)
            {
                Width = data["Width"].AsFloat;
                Height = data["Height"].AsFloat;
                Health = data["Health"].AsFloat;
                HealthRate = data["HealthRate"].AsFloat;
                HealthScale = data["HealthScale"].AsFloat;
            }
        }

        public static volatile Dictionary<FishType, FishPhysicalInfo> FishPhysicalData = new Dictionary<FishType, FishPhysicalInfo>{
            {FishType.Basic, new FishPhysicalInfo(1,1,0) },
            {FishType.Cuttle, new FishPhysicalInfo(46,117,0) },
            {FishType.GoldFish, new FishPhysicalInfo(25,93,0) },
            {FishType.LightenFish, new FishPhysicalInfo(53,96,2500) }, //
            {FishType.Mermaid, new FishPhysicalInfo(53,91,1000) }, //
            {FishType.Octopus, new FishPhysicalInfo(53,43,0) }, //
            {FishType.PufferFish, new FishPhysicalInfo(40,40,200) }, //
            {FishType.SeaFish, new FishPhysicalInfo(53,60,400) }, //
            {FishType.Shark, new FishPhysicalInfo(40,60,600) }, //
            {FishType.Stringray, new FishPhysicalInfo(85,92,1500) },//
            {FishType.Turtle, new FishPhysicalInfo(53,43,300) },
            {FishType.CaThanTai, new FishPhysicalInfo(100,270,20000) }, //
            {FishType.FlyingFish, new FishPhysicalInfo(53,60,500) },
            {FishType.GoldenFrog, new FishPhysicalInfo(233,233,6600) }, //
            {FishType.SeaTurtle, new FishPhysicalInfo(53,60,0) }, //
            {FishType.MerMan, new FishPhysicalInfo(60,180,5000) }, //
            {FishType.Phoenix, new FishPhysicalInfo(135,257,0) },
            {FishType.MermaidBig, new FishPhysicalInfo(53,280,10000) }, //
            {FishType.MermaidSmall, new FishPhysicalInfo(80,200,7000) }, //
            {FishType.BombFish, new FishPhysicalInfo(100,420,6000000) }, //
            {FishType.Fish19, new FishPhysicalInfo(100,420,0) }, //
            {FishType.Fish20, new FishPhysicalInfo(100,420,0) }, //
            {FishType.Fish21, new FishPhysicalInfo(100,420,0) }, //
            {FishType.Fish22, new FishPhysicalInfo(100,420,0) }, //
            {FishType.Fish23, new FishPhysicalInfo(100,420,0) }, //
            {FishType.Fish24, new FishPhysicalInfo(100,420,0) }, //
            {FishType.Fish25, new FishPhysicalInfo(100,420,0) }, //
        };
        public static volatile float MinScale = 1f;
        public static volatile float MaxScale = 1.05f;
        public static volatile float MaxFillScale = 1.2f;
        #endregion

        #region Bullet
        public enum BulletType : int
        {
            Basic = 0,
            Bullet1 = 1,
            Bullet2 = 2,
            Bullet3 = 3,
            Bullet4 = 4,
            Bullet5 = 5,
            Bullet6 = 6
        }

        public static volatile Dictionary<BulletType, int> TypeToPower = new Dictionary<BulletType, int> {
            {BulletType.Basic, 1 },
            {BulletType.Bullet1, 5 },
            {BulletType.Bullet2, 10 },
            {BulletType.Bullet3, 15 },
            {BulletType.Bullet4, 20 },
            {BulletType.Bullet5, 25 },
            {BulletType.Bullet6, 30 }
        };

        public static volatile Dictionary<BulletType, int> TypeToValue = new Dictionary<BulletType, int> {
            {BulletType.Basic, 0 },
            {BulletType.Bullet1, 5 },
            {BulletType.Bullet2, 10 },
            {BulletType.Bullet3, 15 },
            {BulletType.Bullet4, 20 },
            {BulletType.Bullet5, 0 },
            {BulletType.Bullet6, 0 }
        };

        public static volatile Dictionary<BulletType, int> TypeToJpCheckCount = new Dictionary<BulletType, int> {
            {BulletType.Basic, 0 },
            {BulletType.Bullet1, 1 },
            {BulletType.Bullet2, 2 },
            {BulletType.Bullet3, 5 },
            {BulletType.Bullet4, 10 },
            {BulletType.Bullet5, 20 },
            {BulletType.Bullet6, 50 }
        };

        public static volatile int MAX_BOUND_TIME = 4;
        public static volatile float BulletSpeed = 1400;
        public static volatile float BulletRadius = 22;
        #endregion

        #region Player
        public enum QuitReason : int
        {
            Unknown = -1,
            Normal = 0,
            TimeOut = 1,
            Kick = 2,
            Disconnect = 3
        }

        public static volatile float MaxIdleTimeS = 2f * 60;
        // new lv = a * lv3 + b * lv2 + c * lv + d
        private static volatile float a = 8;
        private static volatile float b = 32;
        private static volatile float c = 1024;
        private static volatile float d = 2048;
        public static long GetExpToNextLevel(long lv)
        {
            long lv2 = lv * lv;
            long lv3 = lv2 * lv;
            return (long)(a * lv3 + b * lv2 + c * lv + d);
        }

        // divide user
        private static volatile bool UserCardInOnOff = true;
        private static volatile int UserHighCash = 20000;
        private static volatile int UserMidCash = 10000;

        private static volatile int UserCardInHighCash = 20000;
        private static volatile int UserCardInMidCash = 10000;

        public static volatile float[] MinimumRefundUserBankRate = new float[] { 1.3f, 1.2f, 1.1f }; // low, mid, high
        public static volatile float[] MinimumRefundUserCardInBankRate = new float[] { 1.2f, 1.1f, 1f }; // low, mid, high

        public static float GetMinimumBankRate(long currentCash, long cardIn)
        {
            if(UserCardInOnOff && cardIn > 0) // is user cardin
            {
                var rates = MinimumRefundUserCardInBankRate;
                if (currentCash >= UserCardInHighCash && rates.Length > 2)
                {
                    //Logger.Info("User bc is card in high");
                    return rates[2];
                }
                else if(currentCash >= UserCardInMidCash && rates.Length > 1)
                {
                    //Logger.Info("User bc is card in mid");
                    return rates[1];
                }
                else if (rates.Length > 0)
                {
                    //Logger.Info("User bc is card in low");
                    return rates[0];
                }
                return 1f;
            }
            else
            {
                var rates = MinimumRefundUserBankRate;
                if (currentCash >= UserHighCash && rates.Length > 2)
                {
                    //Logger.Info("User bc is high");
                    return rates[2];
                }
                else if (currentCash >= UserMidCash && rates.Length > 1)
                {
                    //Logger.Info("User bc is mid");
                    return rates[1];
                }
                else if (rates.Length > 0)
                {
                    //Logger.Info("User bc is low");
                    return rates[0];
                }
                return 1f;
            }
        }
        #endregion

        #region json
        public volatile static bool ConfigLoaded = false; // did config load from external

        public static JSONArray NumberArrayToJson<T>(T[] a)
        {
            var arr = new JSONArray();
            for (int i = 0, n = a.Length; i < n; i++)
            {
                arr.Add(a[i].ToString());
            }
            return arr;
        }

        public static int[] JsonToIntArray(JSONArray a)
        {
            var arr = new int[a.Count];
            for (int i = 0, n = a.Count; i < n; i++)
            {
                arr[i] = a[i].AsInt;
            }
            return arr;
        }

        public static float[] JsonToFloatArray(JSONArray a)
        {
            var arr = new float[a.Count];
            for (int i = 0, n = a.Count; i < n; i++)
            {
                arr[i] = a[i].AsFloat;
            }
            return arr;
        }

        public static long[] JsonToLongArray(JSONArray a)
        {
            try
            {
                var arr = new long[a.Count];
                for (int i = 0, n = a.Count; i < n; i++)
                {
                    arr[i] = a[i].AsLong;
                }
                return arr;
            }
            catch (Exception ex)
            {
                Logger.Error(ex.ToString());
                throw ex;
            }
        }

        public static string ToJsonString()
        {
            return ToJson().ToString();
        }

        public static JSONNode ToJson()
        {
            var data = new JSONObject();
            data["TableStartingBlinds"] = NumberArrayToJson(TableStartingBlinds);
            data["TableBulletValueRate"] = NumberArrayToJson(TableBulletValueRate);
            data["TableRequireCardIn"] = NumberArrayToJson(TableRequireCardIn);

            data["NumberOfAccountPerDevice"] = NumberOfAccountPerDevice;
            data["NumberOfAccountPerDay"] = NumberOfAccountPerDay;

            data["RequestSampleRateMs"] = RequestSampleRateMs;
            data["MaxRequestPerSecond"] = MaxRequestPerSecond;
            data["LogShooting"] = LogShooting;
            data["MinTimeBetweenShooting"] = MinTimeBetweenShooting;
            data["TimeBetweenVideoAdsMs0"] = TimeBetweenVideoAdsMs0;
            data["TimeBetweenVideoAdsMs1"] = TimeBetweenVideoAdsMs1;
            data["VideoAdsRewardCount0"] = VideoAdsRewardCount0;
            data["VideoAdsRewardCount1"] = VideoAdsRewardCount1;
            data["VideoAdsRewardType0"] = VideoAdsRewardType0;
            data["VideoAdsRewardType1"] = VideoAdsRewardType1;
            data["KickDuplicateUsers"] = KickDuplicateUsers;
            data["HideFishHp"] = HideFishHp;

            data["PowerUpIntervalS"] = PowerUpIntervalS;
            data["PowerUpMinPlayTimeS"] = PowerUpMinPlayTimeS;
            data["PowerUpDurationS"] = PowerUpDurationS;
            data["BombRate"] = BombRate;
            data["BonusRate"] = BonusRate;

            data["FastFireCoolDownS"] = FastFireCoolDownS;
            data["FastFireRate"] = FastFireRate;
            data["FastFireDuration"] = FastFireDuration;
            data["FastFireCost"] = FastFireCost;
            data["SnipeCoolDownS"] = SnipeCoolDownS;
            data["SnipeDurationS"] = SnipeDurationS;
            data["SnipeCost"] = SnipeCost;

            data["FeeRate"] = FeeRate;
            data["BombThreshold"] = BombThreshold;
            data["JackpotInitial"] = JackpotInitial;
            data["JackpotRate"] = JackpotRate;

            data["FIRE_RATE"] = FIRE_RATE;
            data["TURN_TIME"] = TURN_TIME;
            data["MIN_SPEED_2"] = MIN_SPEED_2;
            data["MAX_JUMP_SPEED"] = MAX_JUMP_SPEED;
            data["MAX_JUMP_SPEED_2"] = MAX_JUMP_SPEED_2;
            data["MAX_SHADOW_SPEED_2"] = MAX_SHADOW_SPEED_2;
            data["TELEPORT_SHADOW_SPEED_2"] = TELEPORT_SHADOW_SPEED_2;
            data["JUMP_ACCELERATION_OCTOPUS"] = JUMP_ACCELERATION_OCTOPUS;
            data["JUMP_DECCELERATION_OCTOPUS"] = JUMP_DECCELERATION_OCTOPUS;
            data["JUMP_ACCELERATION_CUTTLE"] = JUMP_ACCELERATION_CUTTLE;
            data["JUMP_DECCELERATION_CUTTLE"] = JUMP_DECCELERATION_CUTTLE;
            data["JUMP_ACCELERATION_SEA_TURTLE"] = JUMP_ACCELERATION_SEA_TURTLE;
            data["JUMP_DECCELERATION_SEA_TURTLE"] = JUMP_DECCELERATION_SEA_TURTLE;
            data["MIN_SPEED"] = MIN_SPEED;
            data["MAX_VARY_SPEED"] = MAX_VARY_SPEED;
            data["TURN_ANGLE_RAD"] = TURN_ANGLE_RAD;
            data["JUMP_TURN_ANGLE_RAD"] = JUMP_TURN_ANGLE_RAD;

            data["MAX_BOUND_TIME"] = MAX_BOUND_TIME;
            data["BulletSpeed"] = BulletSpeed;
            data["BulletRadius"] = BulletRadius;

            data["MaxIdleTimeS"] = MaxIdleTimeS;
            data["a"] = a;
            data["b"] = b;
            data["c"] = c;
            data["d"] = d;

            data["MinScale"] = MinScale;
            data["MaxScale"] = MaxScale;
            data["MaxFillScale"] = MaxFillScale;

            data["NUMBER_OF_BULLETS"] = NUMBER_OF_BULLETS;
            data["NUMBER_OF_OBJECTS"] = NUMBER_OF_OBJECTS;
            data["PLAYING_WAVE_DURATION"] = PLAYING_WAVE_DURATION;
            data["NEW_WAVE_MAX_TIME"] = NEW_WAVE_MAX_TIME;
            data["SOLO_DURATION"] = SOLO_DURATION;
            data["WAITING_WAVE_DURATION"] = WAITING_WAVE_DURATION;
            data["GUN_LENGTH"] = GUN_LENGTH;

            var gfWinRate = new JSONArray();
            for (int i = 0, n = GOLDEN_FROG_WIN_RATE.Count; i < n; i++)
            {
                gfWinRate.Add(GOLDEN_FROG_WIN_RATE[i]);
            }
            data["GOLDEN_FROG_WIN_RATE"] = gfWinRate;

            var gfWinMul = new JSONArray();
            for (int i = 0, n = GOLDEN_FROG_WIN_MULTIPLE.Count; i < n; i++)
            {
                gfWinMul.Add(GOLDEN_FROG_WIN_MULTIPLE[i]);
            }
            data["GOLDEN_FROG_WIN_MULTIPLE"] = gfWinMul;

            data["ScreenW"] = ScreenW;
            data["ScreenH"] = ScreenH;
            data["ScreenX"] = ScreenX;
            data["ScreenY"] = ScreenY;
            data["WorldW"] = WorldW;
            data["WorldH"] = WorldH;
            data["WorldX"] = WorldX;
            data["WorldY"] = WorldY;
            data["OutWorldW"] = OutWorldW;
            data["OutWorldH"] = OutWorldH;
            data["OutWorldX"] = OutWorldX;
            data["OutWorldY"] = OutWorldY;
            data["SpawnW"] = SpawnW;
            data["SpawnH"] = SpawnH;
            data["SpawnX"] = SpawnX;
            data["SpawnY"] = SpawnY;

            data["SERVER_UPDATE_LOOP_MS"] = SERVER_UPDATE_LOOP_MS;
            data["CLIENT_UPDATE_S"] = CLIENT_UPDATE_S;

            var playerPosA = new JSONArray();
            for (int i = 0, n = playerPos.Length; i < n; i++)
            {
                playerPosA.Add(playerPos[i].ToJson());
            }
            data["playerPos"] = playerPosA;

            var bp = new JSONObject();
            bp[BulletType.Basic.ToString()] = TypeToPower[BulletType.Basic];
            bp[BulletType.Bullet1.ToString()] = TypeToPower[BulletType.Bullet1];
            bp[BulletType.Bullet2.ToString()] = TypeToPower[BulletType.Bullet2];
            bp[BulletType.Bullet3.ToString()] = TypeToPower[BulletType.Bullet3];
            bp[BulletType.Bullet4.ToString()] = TypeToPower[BulletType.Bullet4];
            bp[BulletType.Bullet5.ToString()] = TypeToPower[BulletType.Bullet5];
            bp[BulletType.Bullet6.ToString()] = TypeToPower[BulletType.Bullet6];
            data["TypeToPower"] = bp;

            var bp2 = new JSONObject();
            bp2[BulletType.Basic.ToString()] = TypeToValue[BulletType.Basic];
            bp2[BulletType.Bullet1.ToString()] = TypeToValue[BulletType.Bullet1];
            bp2[BulletType.Bullet2.ToString()] = TypeToValue[BulletType.Bullet2];
            bp2[BulletType.Bullet3.ToString()] = TypeToValue[BulletType.Bullet3];
            bp2[BulletType.Bullet4.ToString()] = TypeToValue[BulletType.Bullet4];
            bp2[BulletType.Bullet5.ToString()] = TypeToValue[BulletType.Bullet5];
            bp2[BulletType.Bullet6.ToString()] = TypeToValue[BulletType.Bullet6];
            data["TypeToValue"] = bp2;

            var fd = new JSONObject();
            fd[FishType.Basic.ToString()] = FishPhysicalData[FishType.Basic].ToJson();
            fd[FishType.Cuttle.ToString()] = FishPhysicalData[FishType.Cuttle].ToJson();
            fd[FishType.GoldFish.ToString()] = FishPhysicalData[FishType.GoldFish].ToJson();
            fd[FishType.LightenFish.ToString()] = FishPhysicalData[FishType.LightenFish].ToJson();
            fd[FishType.Mermaid.ToString()] = FishPhysicalData[FishType.Mermaid].ToJson();
            fd[FishType.Octopus.ToString()] = FishPhysicalData[FishType.Octopus].ToJson();
            fd[FishType.PufferFish.ToString()] = FishPhysicalData[FishType.PufferFish].ToJson();
            fd[FishType.SeaFish.ToString()] = FishPhysicalData[FishType.SeaFish].ToJson();
            fd[FishType.Shark.ToString()] = FishPhysicalData[FishType.Shark].ToJson();
            fd[FishType.Stringray.ToString()] = FishPhysicalData[FishType.Stringray].ToJson();
            fd[FishType.Turtle.ToString()] = FishPhysicalData[FishType.Turtle].ToJson();
            fd[FishType.CaThanTai.ToString()] = FishPhysicalData[FishType.CaThanTai].ToJson();
            fd[FishType.FlyingFish.ToString()] = FishPhysicalData[FishType.FlyingFish].ToJson();
            fd[FishType.GoldenFrog.ToString()] = FishPhysicalData[FishType.GoldenFrog].ToJson();
            fd[FishType.SeaTurtle.ToString()] = FishPhysicalData[FishType.SeaTurtle].ToJson();
            fd[FishType.MerMan.ToString()] = FishPhysicalData[FishType.MerMan].ToJson();
            fd[FishType.Phoenix.ToString()] = FishPhysicalData[FishType.Phoenix].ToJson();
            fd[FishType.MermaidBig.ToString()] = FishPhysicalData[FishType.MermaidBig].ToJson();
            fd[FishType.MermaidSmall.ToString()] = FishPhysicalData[FishType.MermaidSmall].ToJson();
            fd[FishType.BombFish.ToString()] = FishPhysicalData[FishType.BombFish].ToJson();
            fd[FishType.Fish19.ToString()] = FishPhysicalData[FishType.Fish19].ToJson();
            fd[FishType.Fish20.ToString()] = FishPhysicalData[FishType.Fish20].ToJson();
            fd[FishType.Fish21.ToString()] = FishPhysicalData[FishType.Fish21].ToJson();
            fd[FishType.Fish22.ToString()] = FishPhysicalData[FishType.Fish22].ToJson();
            fd[FishType.Fish23.ToString()] = FishPhysicalData[FishType.Fish23].ToJson();
            fd[FishType.Fish24.ToString()] = FishPhysicalData[FishType.Fish24].ToJson();
            fd[FishType.Fish25.ToString()] = FishPhysicalData[FishType.Fish25].ToJson();
            data["FishPhysicalData"] = fd;

            var fakeJpMin = new JSONObject();
            data["FakeJpMinTimeMS"] = fakeJpMin;
            foreach (var item in FakeJpMinTimeMS)
            {
                fakeJpMin[item.Key.ToString()] = item.Value;
            }

            var fakeJpMax = new JSONObject();
            data["FakeJpMaxTimeMS"] = fakeJpMax;
            foreach (var item in FakeJpMaxTimeMS)
            {
                fakeJpMax[item.Key.ToString()] = item.Value;
            }

            var fakeMinAddJp = new JSONObject();
            data["MinAddFakeJp"] = fakeMinAddJp;
            foreach (var item in MinAddFakeJp)
            {
                fakeMinAddJp[item.Key.ToString()] = item.Value;
            }

            var fakeMaxAddJp = new JSONObject();
            data["MaxAddFakeJp"] = fakeMaxAddJp;
            foreach (var item in MaxAddFakeJp)
            {
                fakeMaxAddJp[item.Key.ToString()] = item.Value;
            }

            data["SoloFee"] = SoloFee;
            data["SoloSnipe"] = SoloSnipe;
            data["SoloFastFire"] = SoloFastFire;
            data["SoloBomb"] = SoloBomb;
            data["SoloItemBombDamage"] = SoloItemBombDamage;

            data["TableSoloCashIn"] = NumberArrayToJson(TableSoloCashIn);
            data["TableSoloVirtualCash"] = NumberArrayToJson(TableSoloVirtualCash);

            data["UserCardInOnOff"] = UserCardInOnOff ? 1 : 0;
            data["UserHighCash"] = UserHighCash;
            data["UserMidCash"] = UserMidCash;
            data["UserCardInHighCash"] = UserCardInHighCash;
            data["UserCardInMidCash"] = UserCardInMidCash;

            data["MinimumRefundUserBankRate"] = NumberArrayToJson(MinimumRefundUserBankRate);
            data["MinimumRefundUserCardInBankRate"] = NumberArrayToJson(MinimumRefundUserCardInBankRate);

            return data;
        }

        public static void ParseJson(JSONNode data)
        {
            if (data.HasKey("TableStartingBlinds")) TableStartingBlinds = JsonToLongArray(data["TableStartingBlinds"].AsArray);
            if (data.HasKey("TableBulletValueRate")) TableBulletValueRate = JsonToIntArray(data["TableBulletValueRate"].AsArray);
            if (data.HasKey("TableRequireCardIn")) TableRequireCardIn = JsonToLongArray(data["TableRequireCardIn"].AsArray);

            if (data.HasKey("NumberOfAccountPerDevice")) NumberOfAccountPerDevice = data["NumberOfAccountPerDevice"].AsInt;
            if (data.HasKey("NumberOfAccountPerDay")) NumberOfAccountPerDay = data["NumberOfAccountPerDay"].AsInt;

            if (data.HasKey("RequestSampleRateMs")) RequestSampleRateMs = data["RequestSampleRateMs"].AsInt;
            if (data.HasKey("MaxRequestPerSecond")) MaxRequestPerSecond = data["MaxRequestPerSecond"].AsFloat;
            if (data.HasKey("LogShooting")) LogShooting = data["LogShooting"].AsInt;
            if (data.HasKey("MinTimeBetweenShooting")) MinTimeBetweenShooting = data["MinTimeBetweenShooting"].AsInt;

            if (data.HasKey("TimeBetweenVideoAdsMs0")) TimeBetweenVideoAdsMs0 = data["TimeBetweenVideoAdsMs0"].AsInt;
            if (data.HasKey("TimeBetweenVideoAdsMs1")) TimeBetweenVideoAdsMs1 = data["TimeBetweenVideoAdsMs1"].AsInt;
            if (data.HasKey("VideoAdsRewardCount0")) VideoAdsRewardCount0 = data["VideoAdsRewardCount0"].AsInt;
            if (data.HasKey("VideoAdsRewardCount1")) VideoAdsRewardCount1 = data["VideoAdsRewardCount1"].AsInt;
            if (data.HasKey("VideoAdsRewardType0")) VideoAdsRewardType0 = data["VideoAdsRewardType0"].AsInt;
            if (data.HasKey("VideoAdsRewardType1")) VideoAdsRewardType1 = data["VideoAdsRewardType1"].AsInt;
            if (data.HasKey("KickDuplicateUsers")) KickDuplicateUsers = data["KickDuplicateUsers"].AsInt;
            if (data.HasKey("HideFishHp")) HideFishHp = data["HideFishHp"].AsInt;

            if (data.HasKey("PowerUpIntervalS")) PowerUpIntervalS = data["PowerUpIntervalS"].AsFloat;
            if (data.HasKey("PowerUpMinPlayTimeS")) PowerUpMinPlayTimeS = data["PowerUpMinPlayTimeS"].AsFloat;
            if (data.HasKey("PowerUpDurationS")) PowerUpDurationS = data["PowerUpDurationS"].AsFloat;
            if (data.HasKey("BombRate")) BombRate = data["BombRate"].AsInt;
            if (data.HasKey("BonusRate")) BonusRate = data["BonusRate"].AsInt;

            if (data.HasKey("FastFireCoolDownS")) FastFireCoolDownS = data["FastFireCoolDownS"].AsFloat;
            if (data.HasKey("FastFireRate")) FastFireRate = data["FastFireRate"].AsFloat;
            if (data.HasKey("FastFireDuration")) FastFireDuration = data["FastFireDuration"].AsFloat;
            if (data.HasKey("FastFireCost")) FastFireCost = data["FastFireCost"].AsInt;
            if (data.HasKey("SnipeCoolDownS")) SnipeCoolDownS = data["SnipeCoolDownS"].AsFloat;
            if (data.HasKey("SnipeDurationS")) SnipeDurationS = data["SnipeDurationS"].AsFloat;
            if (data.HasKey("SnipeCost")) SnipeCost = data["SnipeCost"].AsInt;

            if (data.HasKey("FeeRate")) FeeRate = data["FeeRate"].AsFloat;
            if (data.HasKey("BombThreshold")) BombThreshold = data["BombThreshold"].AsFloat;
            if (data.HasKey("JackpotInitial")) JackpotInitial = data["JackpotInitial"].AsFloat;
            if (data.HasKey("JackpotRate")) JackpotRate = data["JackpotRate"].AsFloat;

            if (data.HasKey("FIRE_RATE")) FIRE_RATE = data["FIRE_RATE"].AsFloat;
            if (data.HasKey("TURN_TIME")) TURN_TIME = data["TURN_TIME"].AsFloat;
            if (data.HasKey("MIN_SPEED_2")) MIN_SPEED_2 = data["MIN_SPEED_2"].AsFloat;
            if (data.HasKey("MAX_JUMP_SPEED")) MAX_JUMP_SPEED = data["MAX_JUMP_SPEED"].AsFloat;
            if (data.HasKey("MAX_JUMP_SPEED_2")) MAX_JUMP_SPEED_2 = data["MAX_JUMP_SPEED_2"].AsFloat;
            if (data.HasKey("MAX_SHADOW_SPEED_2")) MAX_SHADOW_SPEED_2 = data["MAX_SHADOW_SPEED_2"].AsFloat;
            if (data.HasKey("TELEPORT_SHADOW_SPEED_2")) TELEPORT_SHADOW_SPEED_2 = data["TELEPORT_SHADOW_SPEED_2"].AsFloat;
            if (data.HasKey("JUMP_ACCELERATION_OCTOPUS")) JUMP_ACCELERATION_OCTOPUS = data["JUMP_ACCELERATION_OCTOPUS"].AsFloat;
            if (data.HasKey("JUMP_DECCELERATION_OCTOPUS")) JUMP_DECCELERATION_OCTOPUS = data["JUMP_DECCELERATION_OCTOPUS"].AsFloat;
            if (data.HasKey("JUMP_ACCELERATION_CUTTLE")) JUMP_ACCELERATION_CUTTLE = data["JUMP_ACCELERATION_CUTTLE"].AsFloat;
            if (data.HasKey("JUMP_DECCELERATION_CUTTLE")) JUMP_DECCELERATION_CUTTLE = data["JUMP_DECCELERATION_CUTTLE"].AsFloat;
            if (data.HasKey("JUMP_ACCELERATION_SEA_TURTLE")) JUMP_ACCELERATION_SEA_TURTLE = data["JUMP_ACCELERATION_SEA_TURTLE"].AsFloat;
            if (data.HasKey("JUMP_DECCELERATION_SEA_TURTLE")) JUMP_DECCELERATION_SEA_TURTLE = data["JUMP_DECCELERATION_SEA_TURTLE"].AsFloat;
            if (data.HasKey("MIN_SPEED")) MIN_SPEED = data["MIN_SPEED"].AsFloat;
            if (data.HasKey("MAX_VARY_SPEED")) MAX_VARY_SPEED = data["MAX_VARY_SPEED"].AsFloat;
            if (data.HasKey("TURN_ANGLE_RAD")) TURN_ANGLE_RAD = data["TURN_ANGLE_RAD"].AsFloat;
            if (data.HasKey("JUMP_TURN_ANGLE_RAD")) JUMP_TURN_ANGLE_RAD = data["JUMP_TURN_ANGLE_RAD"].AsFloat;

            if (data.HasKey("MAX_BOUND_TIME")) MAX_BOUND_TIME = data["MAX_BOUND_TIME"].AsInt;
            if (data.HasKey("BulletSpeed")) BulletSpeed = data["BulletSpeed"].AsFloat;
            if (data.HasKey("BulletRadius")) BulletRadius = data["BulletRadius"].AsFloat;
            if (data.HasKey("MaxIdleTimeS")) MaxIdleTimeS = data["MaxIdleTimeS"].AsFloat;

            if (data.HasKey("a")) a = data["a"].AsFloat;
            if (data.HasKey("b")) b = data["b"].AsFloat;
            if (data.HasKey("c")) c = data["c"].AsFloat;
            if (data.HasKey("d")) d = data["d"].AsFloat;

            if (data.HasKey("MinScale")) MinScale = data["MinScale"].AsFloat;
            if (data.HasKey("MaxScale")) MaxScale = data["MaxScale"].AsFloat;
            if (data.HasKey("MaxFillScale")) MaxFillScale = data["MaxFillScale"].AsFloat;

            if (data.HasKey("NUMBER_OF_BULLETS")) NUMBER_OF_BULLETS = data["NUMBER_OF_BULLETS"].AsFloat;
            if (data.HasKey("NUMBER_OF_OBJECTS")) NUMBER_OF_OBJECTS = data["NUMBER_OF_OBJECTS"].AsFloat;
            if (data.HasKey("PLAYING_WAVE_DURATION")) PLAYING_WAVE_DURATION = data["PLAYING_WAVE_DURATION"].AsFloat;
            if (data.HasKey("NEW_WAVE_MAX_TIME")) NEW_WAVE_MAX_TIME = data["NEW_WAVE_MAX_TIME"].AsFloat;
            if (data.HasKey("SOLO_DURATION")) SOLO_DURATION = data["SOLO_DURATION"].AsFloat;
            if (data.HasKey("WAITING_WAVE_DURATION")) WAITING_WAVE_DURATION = data["WAITING_WAVE_DURATION"].AsFloat;
            if (data.HasKey("GUN_LENGTH")) GUN_LENGTH = data["GUN_LENGTH"].AsFloat;

            if (data.HasKey("GOLDEN_FROG_WIN_RATE"))
            {
                var gfWinRate = data["GOLDEN_FROG_WIN_RATE"].AsArray;
                GOLDEN_FROG_WIN_RATE.Clear();
                for (int i = 0, n = gfWinRate.Count; i < n; i++)
                {
                    GOLDEN_FROG_WIN_RATE.Add(gfWinRate[i].AsFloat);
                }
            }

            if (data.HasKey("GOLDEN_FROG_WIN_MULTIPLE"))
            {
                var gfWinMul = data["GOLDEN_FROG_WIN_MULTIPLE"].AsArray;
                GOLDEN_FROG_WIN_MULTIPLE.Clear();
                for (int i = 0, n = gfWinMul.Count; i < n; i++)
                {
                    GOLDEN_FROG_WIN_MULTIPLE.Add(gfWinMul[i].AsFloat);
                }
            }

            if (data.HasKey("ScreenW")) ScreenW = data["ScreenW"].AsFloat;
            if (data.HasKey("ScreenH")) ScreenH = data["ScreenH"].AsFloat;
            if (data.HasKey("ScreenX")) ScreenX = data["ScreenX"].AsFloat;
            if (data.HasKey("ScreenY")) ScreenY = data["ScreenY"].AsFloat;
            if (data.HasKey("WorldW")) WorldW = data["WorldW"].AsFloat;
            if (data.HasKey("WorldH")) WorldH = data["WorldH"].AsFloat;
            if (data.HasKey("WorldX")) WorldX = data["WorldX"].AsFloat;
            if (data.HasKey("WorldY")) WorldY = data["WorldY"].AsFloat;
            if (data.HasKey("OutWorldW")) OutWorldW = data["OutWorldW"].AsFloat;
            if (data.HasKey("OutWorldH")) OutWorldH = data["OutWorldH"].AsFloat;
            if (data.HasKey("OutWorldX")) OutWorldX = data["OutWorldX"].AsFloat;
            if (data.HasKey("OutWorldY")) OutWorldY = data["OutWorldY"].AsFloat;
            if (data.HasKey("SpawnW")) SpawnW = data["SpawnW"].AsFloat;
            if (data.HasKey("SpawnH")) SpawnH = data["SpawnH"].AsFloat;
            if (data.HasKey("SpawnX")) SpawnX = data["SpawnX"].AsFloat;
            if (data.HasKey("SpawnY")) SpawnY = data["SpawnY"].AsFloat;

            if (data.HasKey("SERVER_UPDATE_LOOP_MS")) SERVER_UPDATE_LOOP_MS = data["SERVER_UPDATE_LOOP_MS"].AsInt;
            if (data.HasKey("CLIENT_UPDATE_S")) CLIENT_UPDATE_S = data["CLIENT_UPDATE_S"].AsFloat;

            if (data.HasKey("playerPos"))
            {
                var playerPosA = data["playerPos"].AsArray;
                for (int i = 0, n = playerPosA.Count, m = playerPos.Length; i < n && i < m; i++)
                {
                    playerPos[i].ParseJson(playerPosA[i].AsObject);
                }
            }

            if (data.HasKey("TypeToPower"))
            {
                var bp = data["TypeToPower"].AsObject;
                TypeToPower[BulletType.Basic] = bp[BulletType.Basic.ToString()].AsInt;
                TypeToPower[BulletType.Bullet1] = bp[BulletType.Bullet1.ToString()].AsInt;
                TypeToPower[BulletType.Bullet2] = bp[BulletType.Bullet2.ToString()].AsInt;
                TypeToPower[BulletType.Bullet3] = bp[BulletType.Bullet3.ToString()].AsInt;
                TypeToPower[BulletType.Bullet4] = bp[BulletType.Bullet4.ToString()].AsInt;
                TypeToPower[BulletType.Bullet5] = bp[BulletType.Bullet5.ToString()].AsInt;
                TypeToPower[BulletType.Bullet6] = bp[BulletType.Bullet6.ToString()].AsInt;
            }

            if (data.HasKey("TypeToValue"))
            {
                var bp2 = data["TypeToValue"].AsObject;
                TypeToValue[BulletType.Basic] = bp2[BulletType.Basic.ToString()].AsInt;
                TypeToValue[BulletType.Bullet1] = bp2[BulletType.Bullet1.ToString()].AsInt;
                TypeToValue[BulletType.Bullet2] = bp2[BulletType.Bullet2.ToString()].AsInt;
                TypeToValue[BulletType.Bullet3] = bp2[BulletType.Bullet3.ToString()].AsInt;
                TypeToValue[BulletType.Bullet4] = bp2[BulletType.Bullet4.ToString()].AsInt;
                TypeToValue[BulletType.Bullet5] = bp2[BulletType.Bullet5.ToString()].AsInt;
                TypeToValue[BulletType.Bullet6] = bp2[BulletType.Bullet6.ToString()].AsInt;

                int _base = TypeToValue[BulletType.Bullet1] = bp2[BulletType.Bullet1.ToString()].AsInt;
                TypeToJpCheckCount[BulletType.Bullet1] = 1; // always 1
                TypeToJpCheckCount[BulletType.Bullet2] = TypeToValue[BulletType.Bullet2] / _base;
                TypeToJpCheckCount[BulletType.Bullet3] = TypeToValue[BulletType.Bullet3] / _base;
                TypeToJpCheckCount[BulletType.Bullet4] = TypeToValue[BulletType.Bullet4] / _base;
                TypeToJpCheckCount[BulletType.Bullet5] = TypeToValue[BulletType.Bullet5] / _base;
                TypeToJpCheckCount[BulletType.Bullet6] = TypeToValue[BulletType.Bullet6] / _base;
            }

            if (data.HasKey("FishPhysicalData"))
            {
                var fd = data["FishPhysicalData"].AsObject;
                FishPhysicalData[FishType.Basic].ParseJson(fd[FishType.Basic.ToString()]);
                FishPhysicalData[FishType.Cuttle].ParseJson(fd[FishType.Cuttle.ToString()]);
                FishPhysicalData[FishType.GoldFish].ParseJson(fd[FishType.GoldFish.ToString()]);
                FishPhysicalData[FishType.LightenFish].ParseJson(fd[FishType.LightenFish.ToString()]);
                FishPhysicalData[FishType.Mermaid].ParseJson(fd[FishType.Mermaid.ToString()]);
                FishPhysicalData[FishType.Octopus].ParseJson(fd[FishType.Octopus.ToString()]);
                FishPhysicalData[FishType.PufferFish].ParseJson(fd[FishType.PufferFish.ToString()]);
                FishPhysicalData[FishType.SeaFish].ParseJson(fd[FishType.SeaFish.ToString()]);
                FishPhysicalData[FishType.Shark].ParseJson(fd[FishType.Shark.ToString()]);
                FishPhysicalData[FishType.Stringray].ParseJson(fd[FishType.Stringray.ToString()]);
                FishPhysicalData[FishType.Turtle].ParseJson(fd[FishType.Turtle.ToString()]);
                FishPhysicalData[FishType.CaThanTai].ParseJson(fd[FishType.CaThanTai.ToString()]);
                FishPhysicalData[FishType.FlyingFish].ParseJson(fd[FishType.FlyingFish.ToString()]);
                FishPhysicalData[FishType.GoldenFrog].ParseJson(fd[FishType.GoldenFrog.ToString()]);
                FishPhysicalData[FishType.SeaTurtle].ParseJson(fd[FishType.SeaTurtle.ToString()]);
                FishPhysicalData[FishType.MerMan].ParseJson(fd[FishType.MerMan.ToString()]);
                FishPhysicalData[FishType.Phoenix].ParseJson(fd[FishType.Phoenix.ToString()]);
                FishPhysicalData[FishType.MermaidBig].ParseJson(fd[FishType.MermaidBig.ToString()]);
                FishPhysicalData[FishType.MermaidSmall].ParseJson(fd[FishType.MermaidSmall.ToString()]);
                FishPhysicalData[FishType.BombFish].ParseJson(fd[FishType.BombFish.ToString()]);
                FishPhysicalData[FishType.Fish19].ParseJson(fd[FishType.Fish19.ToString()]);
                FishPhysicalData[FishType.Fish20].ParseJson(fd[FishType.Fish20.ToString()]);
                FishPhysicalData[FishType.Fish21].ParseJson(fd[FishType.Fish21.ToString()]);
                FishPhysicalData[FishType.Fish22].ParseJson(fd[FishType.Fish22.ToString()]);
                FishPhysicalData[FishType.Fish23].ParseJson(fd[FishType.Fish23.ToString()]);
                FishPhysicalData[FishType.Fish24].ParseJson(fd[FishType.Fish24.ToString()]);
                FishPhysicalData[FishType.Fish25].ParseJson(fd[FishType.Fish25.ToString()]);
            }

            if (data.HasKey("FakeJpMinTimeMS"))
            {
                var fakeJpMin = data["FakeJpMinTimeMS"].AsObject;
                foreach (var item in fakeJpMin.Keys)
                {
                    FakeJpMinTimeMS[int.Parse(item)] = fakeJpMin[item].AsInt;
                }
            }

            if (data.HasKey("FakeJpMaxTimeMS"))
            {
                var fakeJpMax = data["FakeJpMaxTimeMS"].AsObject;
                foreach (var item in fakeJpMax.Keys)
                {
                    FakeJpMaxTimeMS[int.Parse(item)] = fakeJpMax[item].AsInt;
                }
            }

            if (data.HasKey("MinAddFakeJp"))
            {
                var fakeAddJpMin = data["MinAddFakeJp"].AsObject;
                foreach (var item in fakeAddJpMin.Keys)
                {
                    MinAddFakeJp[int.Parse(item)] = fakeAddJpMin[item].AsInt;
                }
            }
            if (data.HasKey("MaxAddFakeJp"))
            {
                var fakeAddJpMax = data["MaxAddFakeJp"].AsObject;
                foreach (var item in fakeAddJpMax.Keys)
                {
                    MaxAddFakeJp[int.Parse(item)] = fakeAddJpMax[item].AsInt;
                }
            }

            if (data.HasKey("SoloFee")) SoloFee = data["SoloFee"].AsFloat;
            if (data.HasKey("SoloSnipe")) SoloSnipe = data["SoloSnipe"].AsInt;
            if (data.HasKey("SoloFastFire")) SoloFastFire = data["SoloFastFire"].AsInt;
            if (data.HasKey("SoloBomb")) SoloBomb = data["SoloBomb"].AsInt;
            if (data.HasKey("SoloItemBombDamage")) SoloItemBombDamage = data["SoloItemBombDamage"].AsInt;

            if (data.HasKey("TableSoloCashIn")) TableSoloCashIn = JsonToIntArray(data["TableSoloCashIn"].AsArray);
            if (data.HasKey("TableSoloVirtualCash")) TableSoloVirtualCash = JsonToIntArray(data["TableSoloVirtualCash"].AsArray);


            if (data.HasKey("UserCardInOnOff")) UserCardInOnOff = data["UserCardInOnOff"].AsInt == 1 ? true : false;
            if (data.HasKey("UserHighCash")) UserHighCash = data["UserHighCash"].AsInt;
            if (data.HasKey("UserMidCash")) UserMidCash = data["UserMidCash"].AsInt;
            if (data.HasKey("UserCardInHighCash")) UserCardInHighCash = data["UserCardInHighCash"].AsInt;
            if (data.HasKey("UserCardInMidCash")) UserCardInMidCash = data["UserCardInMidCash"].AsInt;

            if (data.HasKey("MinimumRefundUserBankRate")) MinimumRefundUserBankRate = JsonToFloatArray(data["MinimumRefundUserBankRate"].AsArray);
            if (data.HasKey("MinimumRefundUserCardInBankRate")) MinimumRefundUserCardInBankRate = JsonToFloatArray(data["MinimumRefundUserCardInBankRate"].AsArray);

            if (OnConfigChange != null)
            {
                OnConfigChange();
            }
        }
        #endregion

        #region Solo
        public static volatile int[] TableSoloCashIn = new int[] { 1, 500, 5000, 50000 };
        public static volatile int[] TableSoloVirtualCash = new int[] { 1, 5000, 50000, 500000 };
        public static volatile float SoloFee = 0.05f;
        public static volatile int SoloSnipe = 3;
        public static volatile int SoloFastFire = 3;
        public static volatile int SoloBomb = 3;
        public static volatile int SoloItemBombDamage = 300;
        #endregion
    }
}
