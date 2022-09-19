using System;
using System.IO;
using Utility.CommandLine;

namespace labeller
{
    class Program
    {
        [Argument('p', "NumberOfPages")]
        private static int NumberOfPages { get; set; }

        [Argument('l', "NumberOfLabelsPerPage")]
        private static int NumberOfLabelsPerPage { get; set; }

        [Argument('f', "FirstLabelNumber")]
        private static int FirstLabelNumber { get; set; }

        static void Main(string[] args)
        {
            Arguments.Populate();

            Console.WriteLine("Number of pages:           " + NumberOfPages);
            Console.WriteLine("Number of labels per page: " + NumberOfLabelsPerPage);
            Console.WriteLine("First label number:        " + FirstLabelNumber);

            if(!(NumberOfPages>0 && NumberOfLabelsPerPage>0 && FirstLabelNumber>0)) {
                Console.WriteLine("");
                Console.WriteLine("/!\\ At least one parameter has a wrong value. /!\\ ");
                Console.WriteLine("");
                Console.WriteLine("Usage: dotnet run Program.cs [parameters]");
                Console.WriteLine("");
                Console.WriteLine("Parameters, all mandatory:");
                Console.WriteLine("");
                Console.WriteLine("-NumberOfPages            Number of pages.");
                Console.WriteLine("-NumberOfLabelsPerPage    Number of labels per page.");
                Console.WriteLine("-FirstLabelNumber         First label number.");
                Console.WriteLine("");
                Console.WriteLine("Example:");
                Console.WriteLine("");
                Console.WriteLine("dotnet run Program.cs --NumberOfPages 2 --NumberOfLabelsPerPage 5 --FirstLabelNumber 2");               
                Console.WriteLine("");
                Environment.Exit(0);
            }

            try
            {
                var folderName = "../labeller_generated_labels";
                if (!Directory.Exists(folderName)) {
                    Directory.CreateDirectory(folderName);
                }
                for (int j = 0; j < NumberOfPages; j++)
                {
                    var filename = $"{folderName}/uuids_{FirstLabelNumber}_{(NumberOfLabelsPerPage+FirstLabelNumber-1)}.csv";
                    StreamWriter sw = new StreamWriter(filename);
                    Console.WriteLine($"Page {j+1}: {filename}");
                    sw.WriteLine("title,content");
                    for (int i = FirstLabelNumber; i < (FirstLabelNumber+NumberOfLabelsPerPage); i++)
                    {
                        var title = $"{i:000}";
                        sw.WriteLine($"{title},\"cookandfreeze://{{'version':1,'title':'{title}','uuid':'{Guid.NewGuid()}'}}\"");
                    }
                    sw.Close();
                    FirstLabelNumber=FirstLabelNumber+NumberOfLabelsPerPage;
                }
                Console.WriteLine("");
                Console.WriteLine("End.");
            }
            catch(Exception e)
            {
                Console.WriteLine("Exception: " + e.Message);
            }
        }
    }
}
