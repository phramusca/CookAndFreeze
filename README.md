# CookAndFreeze

With `CookAndFreeze` for Android, easily manage recipients (content and frozen date) stored in your freezer (or elsewhere).

## Creating self-adhesive labels

- Create CSV files with your labels info using C# application from `labels/labeller` folder:

```powershell
dotnet run labels/labeller/Program.cs --NumberOfLabelsPerPage 7 --FirstLabelNumber 2
```

Example of a generated file with `--NumberOfLabelsPerPage 5 --FirstLabelNumber 2` arguments:

```csv
title,content
002,"cookandfreeze://{'version':1,'title':'002','uuid':'a5834fc0-ab30-476a-871c-54a6bb9b769d'}"
003,"cookandfreeze://{'version':1,'title':'003','uuid':'afd19355-eec5-4616-9876-ce4c6676c3dc'}"
004,"cookandfreeze://{'version':1,'title':'004','uuid':'75677a16-43c9-4573-bc59-8f064209302d'}"
005,"cookandfreeze://{'version':1,'title':'005','uuid':'1a20298f-fce1-4680-b909-c570d48cd08f'}"
006,"cookandfreeze://{'version':1,'title':'006','uuid':'34395a27-c253-4f00-9d2f-2afcef51f99a'}"

```

Of course, you can create those manually (you can get generate uuids with [https://www.guidgenerator.com/](https://www.guidgenerator.com/)).

- Use GLabels to print your labels:
  - Open a glabel project:
    - If you are using "24 Etiquettes Congélation" Ref. 316656 from Casino, you can use [labels/CookAndFreeze.glabels](labels/CookAndFreeze.glabels) file
    - Otherwise, create a new glabel project (there are plenty of available templates or you can create your own quite easily)
  - Open the appropriate CSV file generated earlier with "Objets/Propriétés de fusion" menu
  - If you have created your own glabel file:
    - Use "{title}" as text in a text object (to display the title).
    - Use "content" as data key in a barcode object.
  - Print

Note:

- I am using the GLabels Flathub package (org.gnome.glabels3) from ubuntu repository.
- The [version 4](https://github.com/jimevins/glabels-qt) is not yet released as of 2022 Sept. 16.

## Usage

Now that you have printed and sticked your labels to your recipients, you can scan them using CookAndFreeze with your smartphone.

You can then store their content and frozen date so that you can manage your meals easily.