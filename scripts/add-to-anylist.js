require("dotenv").config();

const AnyList = require("anylist");

async function main() {
  const input = await new Promise(resolve => {
    let data = "";
    process.stdin.on("data", chunk => data += chunk);
    process.stdin.on("end", () => resolve(data));
  });

  const items = JSON.parse(input);

  const any = new AnyList({
    email: process.env.ANYLIST_EMAIL,
    password: process.env.ANYLIST_PASSWORD,
  });

  await any.login(false);

  await any.getLists();

  const list = any.getListByName("Anylist Test");

  for (const item of items) {
    await list.addItem(
      any.createItem({
        name: item.ingredient,
        quantity: `${item.quantity} ${item.unit}`,
      })
    );

    console.log(`Added ${item.ingredient}`);
  }
}

main();
