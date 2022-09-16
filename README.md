# CookAndFreeze

This Android application serves at managing your recipients stored in your freezer (or elsewhere).

## Creating self-adhesive labels

- Create CSV files with your labels info:
  - Ex: If you have 6 labels per page, create one file for your first 6 labels, one file for the next 6, etc ...

```csv
title,content
001,"cookandfreeze://{'version':1,'title':'001','uuid':'f1cb5ea9-7bee-4980-a230-8dfc1d416e6b'}"
002,"cookandfreeze://{'version':1,'title':'002','uuid':'ecc06de0-3d45-4481-891e-af20bc49f4bf'}"
003,"cookandfreeze://{'version':1,'title':'003','uuid':'fb56fb22-3cc5-4e3f-8460-1a14c26eb884'}"
004,"cookandfreeze://{'version':1,'title':'004','uuid':'09d95160-28cc-417b-8b63-f9db1a245a04'}"
005,"cookandfreeze://{'version':1,'title':'005','uuid':'c4fea8f2-822a-46ee-8f59-4573fc6a5c90'}"
006,"cookandfreeze://{'version':1,'title':'006','uuid':'13a5502d-32c4-4210-b0b6-94ace657c63e'}"
```
  
- Use GLabels to generate and print your labels:
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