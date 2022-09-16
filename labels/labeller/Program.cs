using System;
using System.IO;
using Utility.CommandLine;

namespace labeller
{
    class Program
    {
        // [Argument(short name (char), long name (string), help text)]
        [Argument('n', "NumberOfLabelsPerPage", "a boolean value")]
        private static int NumberOfLabelsPerPage { get; set; }

        [Argument('f', "FirstLabelNumber")]
        private static int FirstLabelNumber { get; set; }

        static void Main(string[] args)
        {
            Arguments.Populate();

            Console.WriteLine("NumberOfLabelsPerPage: " + NumberOfLabelsPerPage);
            Console.WriteLine("FirstLabelNumber: " + FirstLabelNumber);

            if(!(NumberOfLabelsPerPage>0 && FirstLabelNumber>0)) {
                Console.WriteLine("");
                Console.WriteLine("/!\\ Missing a parameter. /!\\ ");
                Console.WriteLine("");
                Environment.Exit(0);
            }

            try
            {
                StreamWriter sw = new StreamWriter($"uuids_{FirstLabelNumber}_{(NumberOfLabelsPerPage+FirstLabelNumber-1)}.csv");
                sw.WriteLine("title,content");
                for (int i = FirstLabelNumber; i < (FirstLabelNumber+NumberOfLabelsPerPage); i++)
                {
                    var title = $"{i:000}";
                    sw.WriteLine($"{title},\"cookandfreeze://{{'version':1,'title':'{title}','uuid':'{Guid.NewGuid()}'}}\"");
                }
                sw.Close();
            }
            catch(Exception e)
            {
                Console.WriteLine("Exception: " + e.Message);
            }
            finally
            {
                Console.WriteLine("Executing finally block.");
            }

        }
    }
}
