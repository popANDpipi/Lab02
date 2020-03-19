using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace SurveyServer.Scripts
{
    public class RandomStringBuilder
    {
        private static int _length = 10;
        public static int length
        {
            get
            {
                return _length;
            }
            set
            {
                _length = value;
            }
        }
        private static Random random = new Random((int)DateTime.Now.Ticks);
        /// <summary>
        /// 生成单个随机数字
        /// </summary>
        private static int createNum()
        {
            int num = random.Next(10);
            return num;
        }

        /// <summary>
        /// 生成单个大写随机字母
        /// </summary>
        private static string CreateBigAlpha()
        {
            //A-Z的 ASCII值为65-90
            int num = random.Next(65, 91);
            string abc = Convert.ToChar(num).ToString();
            return abc;
        }

        /// <summary>
        /// 生成单个小写随机字母
        /// </summary>
        private static string CreateSmallAlpha()
        {
            //a-z的 ASCII值为97-122
            int num = random.Next(97, 123);
            string abc = Convert.ToChar(num).ToString();
            return abc;
        }


        /// <summary>
        /// 生成随机字符串
        /// </summary>
        /// <param name="length">字符串的长度</param>
        /// <returns></returns>
        public static string Create(int length)
        {
            // 创建一个StringBuilder对象存储密码
            StringBuilder sb = new StringBuilder();
            //使用for循环把单个字符填充进StringBuilder对象里面变成14位密码字符串
            for (int i = 0; i < length; i++)
            {
                //随机选择里面其中的一种字符生成
                switch (random.Next(3))
                {
                    case 0:
                        //调用生成生成随机数字的方法
                        sb.Append(createNum());
                        break;
                    case 1:
                        //调用生成生成随机小写字母的方法
                        sb.Append(CreateSmallAlpha());
                        break;
                    case 2:
                        //调用生成生成随机大写字母的方法
                        sb.Append(CreateBigAlpha());
                        break;
                }
            }
            return sb.ToString();
        }
        public static string Create()
        {
            return Create((int)length);
        }
        public static string InsertRamdomStr(string str)
        {
            string[] slice = str.Split(".");
            return str.Insert(str.Length - slice[slice.Length - 1].Length - 1, Create());
        }
        public static string ChangeNameToRandomStr(string str)
        {
            string[] slice = str.Split(".");
            return Create() + "." + slice[slice.Length - 1];
        }

    }
}
